/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.Button;
import java.awt.Color;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.SpringLayout;

/**
 * Main core of the project.
 * 
 * @author Griffone
 */
public class Controller {
    
    static Frame explorer, controller, pallete;
    static ImageCanvas imgCanvas;
    static Mandelbrot mandelbrot;
    static MandelbrotDrawer drawer;
    
    public static final int SIDE_PADDING = 5;
    public static final int PADDING = 5;
    public static final int COLUMN_COUNT = 16;
    
    public static final int DEFAULT_WIDTH = 1280, DEFAULT_HEIGHT = 720, DEFAULT_MAX_DEPTH = 64;
    public static final double DEFAULT_X = -2.0/3.0, DEFAULT_Y = 0.0, DEFAULT_SPAN = 3.0;
    public static final double WHEEL_STEP = 1.125;
    
    public static final String DEFAULT_NAME = "mandelbrot.png";
    public static final String IMAGE_PATH_PREFIX = "images/";
    public static final String DEFAULT_EXTENSION = ".png";
    public static final int DEFAULT_SCREENSHOT_WIDTH = 1920, DEFAULT_SCREENSHOT_HEIGHT = 1080;
    
    public static final String ACTION_EDIT_X            = "EX";
    public static final String ACTION_EDIT_Y            = "EY";
    public static final String ACTION_REDUCE_SPAN       = "RS";
    public static final String ACTION_EDIT_SPAN         = "ES";
    public static final String ACTION_INCREASE_SPAN     = "IS";
    public static final String ACTION_REDUCE_DEPTH      = "RD";
    public static final String ACTION_EDIT_DEPTH        = "ED";
    public static final String ACTION_INCREASE_DEPTH    = "ID";
    public static final String ACTION_EDIT_NAME         = "EN";
    public static final String ACTION_SAVE              = "SF";
    public static final String ACTION_SAVE_COORDS       = "SC";
    public static final String ACTION_LOAD_COORDS       = "LC";
    
    static TextField tfX, tfY, tfSpan, tfDepth, tfName;
    
    public static void main(String[] args) {
        mandelbrot = new Mandelbrot(DEFAULT_WIDTH, DEFAULT_HEIGHT, DEFAULT_X, DEFAULT_Y, DEFAULT_SPAN, DEFAULT_MAX_DEPTH);
        initializeExplorer();
        initializeController();
        
        explorer.setLocation(120, 120);
        controller.setLocation(explorer.getWidth() + 240, 120);
        
        drawer = new MandelbrotDrawer(mandelbrot, imgCanvas);
        Thread drawThread = new Thread(drawer);
        drawThread.setDaemon(true);
        mandelbrot.setSpan(DEFAULT_SPAN);
        drawThread.start();
        imgCanvas.setImage(mandelbrot.generate());
    }
    
    public static void zoom(int dz) {
        mandelbrot.setSpan(mandelbrot.getSpan() * Math.pow(WHEEL_STEP, dz));
        tfSpan.setText(Double.toString(mandelbrot.getSpan()));
        drawer.updateImage();
    }
    
    public static void updateRect(int x0, int x1, int beginY, int y) {
        int x = Math.min(x0, x1);
        int width = Math.abs(x0 - x1);
        int height = width * imgCanvas.getHeight() / imgCanvas.getWidth();
        if (beginY > y)
            beginY -= height;
        imgCanvas.updateRect(x, beginY, width, height);
    }
    
    public static void enhance(int x0, int x1, int beginY, int y) {
        int x = Math.min(x0, x1);
        int width = Math.abs(x0 - x1);
        int height = width * imgCanvas.getHeight() / imgCanvas.getWidth();
        if (beginY > y)
            beginY -= height;
        double span = mandelbrot.getSpan() * (double) width / (double) imgCanvas.getWidth();
        // calculate new center
        int dx = x + width / 2 - imgCanvas.getWidth() / 2;
        int dy = beginY + height / 2 - imgCanvas.getHeight() / 2;
        mandelbrot.moveTarget(-dx, -dy);
        mandelbrot.setSpan(span);
        tfX.setText(Double.toString(mandelbrot.getX()));
        tfY.setText(Double.toString(mandelbrot.getY()));
        tfSpan.setText(Double.toString(span));
        drawer.updateImage();
    }
    
    private static void initializeExplorer() {
        explorer = new Frame("Mandelbrot Explorer");
        imgCanvas = new ImageCanvas();
        explorer.setVisible(true);
        Insets insets = explorer.getInsets();
        explorer.setSize(DEFAULT_WIDTH + insets.left + insets.right, DEFAULT_HEIGHT + insets.top + insets.bottom);
        explorer.add(imgCanvas);
        imgCanvas.initializeBuffers();
        
        MouseListener ml = new MouseListener();
        
        imgCanvas.addMouseListener(ml);
        imgCanvas.addMouseMotionListener(ml);
        imgCanvas.addMouseWheelListener(ml);
        explorer.addWindowListener(new CloseWindowListener());
    }
    
    private static void initializeController() {
        // Initialize Controller
        controller = new Frame("Mandelbrot Controller");
        controller.setBackground(Color.LIGHT_GRAY);
        controller.setResizable(false);
        Listener listener = new Listener();
        SpringLayout layout = new SpringLayout();
        controller.setLayout(layout);
        
        // Create rows
        // Row 0: { "x:"[x]_"y":[y] }
        Panel row0 = new Panel();
        {
            SpringLayout spring = new SpringLayout();
            row0.setLayout(spring);
            Label labelX = new Label("X:");
            Label labelY = new Label("Y:");
            tfX = new TextField(COLUMN_COUNT);
            tfX.setName(ACTION_EDIT_X);
            tfX.addActionListener(listener);
            tfX.setText(Double.toString(mandelbrot.getX()));
            tfY = new TextField(COLUMN_COUNT);
            tfY.setName(ACTION_EDIT_Y);
            tfY.addActionListener(listener);
            tfY.setText(Double.toString(mandelbrot.getY()));
            row0.add(labelX);
            row0.add(labelY);
            row0.add(tfX);
            row0.add(tfY);
            spring.putConstraint(SpringLayout.WEST, labelX, 0, SpringLayout.WEST, row0);
            spring.putConstraint(SpringLayout.WEST, tfX, 0, SpringLayout.EAST, labelX);
            spring.putConstraint(SpringLayout.WEST, labelY, PADDING, SpringLayout.EAST, tfX);
            spring.putConstraint(SpringLayout.WEST, tfY, 0, SpringLayout.EAST, labelY);
            spring.putConstraint(SpringLayout.EAST, row0, 0, SpringLayout.EAST, tfY);
            spring.putConstraint(SpringLayout.SOUTH, row0, 0, SpringLayout.SOUTH, tfY);
        }
        
        // Row 1: { ["x1.5"]_[span]_["/1.5"] }
        Panel row1 = new Panel();
        {
            SpringLayout spring = new SpringLayout();
            row1.setLayout(spring);
            Button btnIncreaseSpan = new Button("x1.5");
            btnIncreaseSpan.setName(ACTION_INCREASE_SPAN);
            btnIncreaseSpan.addActionListener(listener);
            tfSpan = new TextField(COLUMN_COUNT);
            tfSpan.setName(ACTION_EDIT_SPAN);
            tfSpan.addActionListener(listener);
            tfSpan.setText(Double.toString(mandelbrot.getSpan()));
            Button btnReduceSpan = new Button("/1.5");
            btnReduceSpan.setName(ACTION_REDUCE_SPAN);
            btnReduceSpan.addActionListener(listener);
            row1.add(btnIncreaseSpan);
            row1.add(tfSpan);
            row1.add(btnReduceSpan);
            spring.putConstraint(SpringLayout.WEST, btnIncreaseSpan, 0, SpringLayout.WEST, row1);
            spring.putConstraint(SpringLayout.WEST, tfSpan, PADDING, SpringLayout.EAST, btnIncreaseSpan);
            spring.putConstraint(SpringLayout.WEST, btnReduceSpan, PADDING, SpringLayout.EAST, tfSpan);
            spring.putConstraint(SpringLayout.EAST, row1, 0, SpringLayout.EAST, btnReduceSpan);
            spring.putConstraint(SpringLayout.SOUTH, row1, 0, SpringLayout.SOUTH, btnReduceSpan);
        }
        
        // Row 2: { ["/2"]_[depth]_["x2"] }
        Panel row2 = new Panel();
        {
            SpringLayout spring = new SpringLayout();
            row2.setLayout(spring);
            Button btnDecreaseDepth = new Button("/2");
            btnDecreaseDepth.setName(ACTION_REDUCE_DEPTH);
            btnDecreaseDepth.addActionListener(listener);
            tfDepth = new TextField(COLUMN_COUNT);
            tfDepth.setName(ACTION_EDIT_DEPTH);
            tfDepth.addActionListener(listener);
            tfDepth.setText(Integer.toString(mandelbrot.getMaxDepth()));
            Button btnIncreaseDepth = new Button("x2");
            btnIncreaseDepth.setName(ACTION_INCREASE_DEPTH);
            btnIncreaseDepth.addActionListener(listener);
            row2.add(btnDecreaseDepth);
            row2.add(tfDepth);
            row2.add(btnIncreaseDepth);
            spring.putConstraint(SpringLayout.WEST, btnDecreaseDepth, 0, SpringLayout.WEST, row2);
            spring.putConstraint(SpringLayout.WEST, tfDepth, PADDING, SpringLayout.EAST, btnDecreaseDepth);
            spring.putConstraint(SpringLayout.WEST, btnIncreaseDepth, PADDING, SpringLayout.EAST, tfDepth);
            spring.putConstraint(SpringLayout.EAST, row2, 0, SpringLayout.EAST, btnIncreaseDepth);
            spring.putConstraint(SpringLayout.SOUTH, row2, 0, SpringLayout.SOUTH, btnIncreaseDepth);
        }
        
        // Row 3: { [filename]_["save"] }
        Panel row3 = new Panel();
        {
            SpringLayout spring = new SpringLayout();
            row3.setLayout(spring);
            tfName = new TextField("mandelbrot.png", COLUMN_COUNT);
            tfName.setName(ACTION_EDIT_NAME);
            tfName.addActionListener(listener);
            tfName.setName(DEFAULT_NAME);
            Button btnSave = new Button("save");
            btnSave.setName(ACTION_SAVE);
            btnSave.addActionListener(listener);
            row3.add(tfName);
            row3.add(btnSave);
            spring.putConstraint(SpringLayout.WEST, tfName, 0, SpringLayout.WEST, row3);
            spring.putConstraint(SpringLayout.WEST, btnSave, PADDING, SpringLayout.EAST, tfName);
            spring.putConstraint(SpringLayout.EAST, row3, 0, SpringLayout.EAST, btnSave);
            spring.putConstraint(SpringLayout.SOUTH, row3, 0, SpringLayout.SOUTH, btnSave);
        }
        
        // Row 4: { ["save coords"]_["load coords"] }
        Panel row4 = new Panel();
        {
            SpringLayout spring = new SpringLayout();
            row4.setLayout(spring);
            Button btnSaveCoords = new Button("save coords");
            btnSaveCoords.setName(ACTION_SAVE_COORDS);
            btnSaveCoords.addActionListener(listener);
            Button btnLoadCoords = new Button("load coords");
            btnLoadCoords.setName(ACTION_LOAD_COORDS);
            btnLoadCoords.addActionListener(listener);
            row4.add(btnSaveCoords);
            row4.add(btnLoadCoords);
            spring.putConstraint(SpringLayout.WEST, btnSaveCoords, 0, SpringLayout.WEST, row4);
            spring.putConstraint(SpringLayout.WEST, btnLoadCoords, PADDING, SpringLayout.EAST, btnSaveCoords);
            spring.putConstraint(SpringLayout.EAST, row4, 0, SpringLayout.EAST, btnLoadCoords);
            spring.putConstraint(SpringLayout.SOUTH, row4, 0, SpringLayout.SOUTH, btnLoadCoords);
        }
        
        controller.add(row0);
        controller.add(row1);
        controller.add(row2);
        controller.add(row3);
        controller.add(row4);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, row0, 0, SpringLayout.HORIZONTAL_CENTER, controller);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, row1, 0, SpringLayout.HORIZONTAL_CENTER, controller);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, row2, 0, SpringLayout.HORIZONTAL_CENTER, controller);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, row3, 0, SpringLayout.HORIZONTAL_CENTER, controller);
        layout.putConstraint(SpringLayout.HORIZONTAL_CENTER, row4, 0, SpringLayout.HORIZONTAL_CENTER, controller);
        
        layout.putConstraint(SpringLayout.NORTH, row0, SIDE_PADDING, SpringLayout.NORTH, controller);
        layout.putConstraint(SpringLayout.NORTH, row1, PADDING, SpringLayout.SOUTH, row0);
        layout.putConstraint(SpringLayout.NORTH, row2, PADDING, SpringLayout.SOUTH, row1);
        layout.putConstraint(SpringLayout.NORTH, row3, PADDING, SpringLayout.SOUTH, row2);
        layout.putConstraint(SpringLayout.NORTH, row4, PADDING, SpringLayout.SOUTH, row3);
        layout.putConstraint(SpringLayout.SOUTH, controller, SIDE_PADDING, SpringLayout.SOUTH, row4);
        
        
        controller.pack();
        int width = 0;
        width = Math.max(width, row0.getWidth());
        width = Math.max(width, row1.getWidth());
        width = Math.max(width, row2.getWidth());
        width = Math.max(width, row3.getWidth());
        width = Math.max(width, row4.getWidth());
        
        controller.setVisible(true);
        Insets insets = controller.getInsets();
        width += insets.left + insets.right + 2 * SIDE_PADDING;
        controller.setSize(width, controller.getHeight());
        controller.addWindowListener(new CloseWindowListener());
    }
    
    static class MouseListener extends MouseAdapter {
        
        private boolean dragEnabled = false;
        private boolean enhancing = false;
        private int lastX, lastY;
        private int beginX, beginY;
        private int enhanceX, enhanceY;
        
        @Override
        public void mousePressed(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3) {
                dragEnabled = true;
                lastX = beginX = e.getX();
                lastY = beginY = e.getY();
                enhancing = false;
                imgCanvas.setRectShown(false);
            } else if (e.getButton() == MouseEvent.BUTTON1) {
                enhanceX = lastX = e.getX();
                enhanceY = lastY = e.getY();
                enhancing = true;
            }
        }
        
        @Override
        public void mouseReleased(MouseEvent e) {
            if (e.getButton() == MouseEvent.BUTTON3&& dragEnabled) {
                dragEnabled = false;
                mandelbrot.moveTarget(e.getX() - beginX, e.getY() - beginY);
                drawer.updateImage();
                tfX.setText(Double.toString(mandelbrot.getX()));
                tfY.setText(Double.toString(mandelbrot.getY()));
            } else if (e.getButton() == MouseEvent.BUTTON1 && enhancing) {
                enhancing = false;
                imgCanvas.setRectShown(false);
                enhance(enhanceX, e.getX(), enhanceY, e.getY());
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragEnabled) {
                imgCanvas.pan(e.getX()- lastX, e.getY() - lastY);
            } else if (enhancing) {
                updateRect(enhanceX, e.getX(), enhanceY, e.getY());
                imgCanvas.setRectShown(true);
            }
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (dragEnabled) {
                dragEnabled = false;
                mandelbrot.moveTarget(e.getX() - beginX, e.getY() - beginY);
                drawer.updateImage();
                tfX.setText(Double.toString(mandelbrot.getX()));
                tfY.setText(Double.toString(mandelbrot.getY()));
            } else if (enhancing) {
                enhancing = false;
                imgCanvas.setRectShown(false);
                enhance(enhanceX, e.getX(), enhanceY, e.getY());
            }
        }
        
        @Override
        public void mouseWheelMoved(MouseWheelEvent e) {
            zoom(e.getWheelRotation());
        }
    }
    
    static class Listener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            String action = ((Component) e.getSource()).getName();
            
            switch (action) {
                case ACTION_REDUCE_DEPTH: {
                    int md = mandelbrot.getMaxDepth() / 2;
                    mandelbrot.setMaxDepth(md);
                    tfDepth.setText(Integer.toString(md));
                    drawer.updateImage();
                } break;
                
                case ACTION_INCREASE_DEPTH: {
                    int md = mandelbrot.getMaxDepth() * 2;
                    mandelbrot.setMaxDepth(md);
                    tfDepth.setText(Integer.toString(md));
                    drawer.updateImage();
                } break;
                
                case ACTION_SAVE: {
                    String name = tfName.getName();
                    if (!name.contains(".")) {
                        System.err.println("No extensioin was provided generating new one.");
                        name = name + ".png";
                        tfName.setName(name);
                    }
                    String subs[] = name.split("\\.");
                    try {
                        ImageIO.write(mandelbrot.generate(DEFAULT_SCREENSHOT_WIDTH, DEFAULT_SCREENSHOT_HEIGHT), subs[subs.length - 1], new File(IMAGE_PATH_PREFIX + name));
                    } catch (IOException ex) {
                        System.err.println("Error saving " + name);
                    }
                } break;
                    
                default:
                    System.err.print("Unsupported command: ");
                    System.err.println(action);
                    break;
            }
        }
    }
    
    static class CloseWindowListener extends WindowAdapter {

        @Override
        public void windowClosing(WindowEvent e) {
            controller.dispose();
            explorer.dispose();
        }
        
    }
}
