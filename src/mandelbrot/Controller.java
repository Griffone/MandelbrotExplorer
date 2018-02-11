/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.Button;
import java.awt.Color;
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
    
    public static final int DEFAULT_WIDTH = 1280;
    public static final int DEFAULT_HEIGHT = 720;
    
    public static final int SIDE_PADDING = 5;
    public static final int PADDING = 5;
    public static final int COLUMN_COUNT = 16;
    
    public static final double WHEEL_STEP = 1.03125;
    
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
    
    static MutableDouble x = new MutableDouble(-1), y = new MutableDouble(0), span = new MutableDouble(4);
    static MutableInteger depth = new MutableInteger(64);
    
    static TextField tfX, tfY, tfSpan, tfDepth, tfName;
    
    public static void main(String[] args) {
        initializeExplorer();
        initializeController();
        
        explorer.setLocation(120, 120);
        controller.setLocation(explorer.getWidth() + 240, 120);
    }
    
    public static void translateXY(int dx, int dy, boolean generateNewImage) {
        int width = imgCanvas.getWidth();
        int height = imgCanvas.getHeight();
        double nx = x.getValue() - (dx * span.getValue() / (double) width);
        double ny = y.getValue() - (dy * span.getValue() / (double) width);
        x.setValue(nx);
        y.setValue(ny);
        if (generateNewImage)
            imgCanvas.setImage(Mandelbrot.generate(width, height, x.getValue(), y.getValue(), span.getValue(), depth.getValue()));
    }
    
    public static void translateXY(int dx, int dy) {
        translateXY(dx, dy, true);
    }
    
    public static void zoom(int dz) {
        span.setValue(span.getValue() * Math.pow(WHEEL_STEP, dz));
        imgCanvas.setImage(Mandelbrot.generate(imgCanvas.getWidth(), imgCanvas.getHeight(), x.getValue(), y.getValue(), span.getValue(), depth.getValue()));
    }
    
    public static void enhance(int left, int top, int right, int bottom) {
        int imgWidth = imgCanvas.getWidth();
        int imgHeight = imgCanvas.getHeight();
        int width = right - left;
        int height = bottom - top;
        int ox = imgWidth / 2;
        int oy = imgHeight / 2;
        int nx = (left + right) / 2;
        int ny = (top + bottom) / 2;
        translateXY(nx - ox, ny - oy, false);
        span.setValue(span.getValue() * (double) width / (double) imgWidth);
        imgCanvas.setImage(Mandelbrot.generate(imgCanvas.getWidth(), imgCanvas.getHeight(), x.getValue(), y.getValue(), span.getValue(), depth.getValue()));
    }
    
    private static void initializeExplorer() {
        explorer = new Frame("Mandelbrot Explorer");
        imgCanvas = new ImageCanvas();
        explorer.setVisible(true);
        Insets insets = explorer.getInsets();
        explorer.setSize(DEFAULT_WIDTH + insets.left + insets.right, DEFAULT_HEIGHT + insets.top + insets.bottom);
        explorer.add(imgCanvas);
        imgCanvas.initializeBuffers();
        imgCanvas.setImage(Mandelbrot.generate(DEFAULT_WIDTH, DEFAULT_HEIGHT, x.getValue(), y.getValue(), span.getValue(), depth.getValue()));
        MouseListener ml = new MouseListener();
        imgCanvas.addMouseListener(ml);
        imgCanvas.addMouseMotionListener(ml);
        imgCanvas.addMouseWheelListener(ml);
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
            tfX.setText(Double.toString(x.getValue()));
            KeyListener kl = new KeyListener(tfX, x);
            kl.connect();
            tfY = new TextField(COLUMN_COUNT);
            tfY.setText(Double.toString(y.getValue()));
            kl = new KeyListener(tfY, y);
            kl.connect();
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
            btnIncreaseSpan.setActionCommand(ACTION_INCREASE_SPAN);
            btnIncreaseSpan.addActionListener(listener);
            tfSpan = new TextField(COLUMN_COUNT);
            tfSpan.setText(Double.toString(span.getValue()));
            KeyListener kl = new KeyListener(tfSpan, span);
            kl.connect();
            Button btnReduceSpan = new Button("/1.5");
            btnReduceSpan.setActionCommand(ACTION_REDUCE_SPAN);
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
            btnDecreaseDepth.setActionCommand(ACTION_REDUCE_DEPTH);
            btnDecreaseDepth.addActionListener(listener);
            tfDepth = new TextField(COLUMN_COUNT);
            tfDepth.setText(Integer.toString(depth.getValue()));
            KeyListener kl = new KeyListener(tfDepth, depth);
            kl.connect();
            Button btnIncreaseDepth = new Button("x2");
            btnIncreaseDepth.setActionCommand(ACTION_INCREASE_DEPTH);
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
            tfName = new TextField("madnelbrot.png", COLUMN_COUNT);
            tfName.addActionListener(listener);
            Button btnSave = new Button("save");
            btnSave.setActionCommand(ACTION_SAVE);
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
            btnSaveCoords.setActionCommand(ACTION_SAVE_COORDS);
            btnSaveCoords.addActionListener(listener);
            Button btnLoadCoords = new Button("load coords");
            btnLoadCoords.setActionCommand(ACTION_LOAD_COORDS);
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
                translateXY(e.getX() - beginX, -(e.getY() - beginY));
            } else if (e.getButton() == MouseEvent.BUTTON1 && enhancing) {
                int ex = e.getX();
                int ey = e.getY();
                enhancing = false;
                imgCanvas.setRectShown(false);
                enhance(Math.min(ex, enhanceX), Math.min(ey, enhanceY), Math.max(ex, enhanceX), Math.max(ey, enhanceY));
            }
        }
        
        @Override
        public void mouseDragged(MouseEvent e) {
            if (dragEnabled) {
                imgCanvas.pan(e.getX() - lastX, e.getY() - lastY);
            } else if (enhancing) {
                imgCanvas.updateRect(enhanceX, enhanceY, e.getX(), e.getY());
                imgCanvas.setRectShown(true);
            }
            lastX = e.getX();
            lastY = e.getY();
        }

        @Override
        public void mouseExited(MouseEvent e) {
            if (dragEnabled) {
                dragEnabled = false;
                translateXY(e.getX() - beginX, -(e.getY() - beginY));
            } else if (enhancing) {
                enhancing = false;
                imgCanvas.setRectShown(false);
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
            String cmd = e.getActionCommand();
            
            switch (cmd) {
                case ACTION_EDIT_X:
                    
                    break;
                    
                case ACTION_EDIT_Y:
                    
                    break;
                    
                case ACTION_REDUCE_SPAN:
                    
                    break;
                    
                case ACTION_EDIT_SPAN:
                    
                    break;
                    
                case ACTION_INCREASE_SPAN:
                    
                    break;
                    
                case ACTION_REDUCE_DEPTH:
                    depth.setValue(depth.getValue() / 2);
                    tfDepth.setText(Integer.toString(depth.getValue()));
                    zoom(0);
                    break;
                    
                case ACTION_EDIT_DEPTH:
                    
                    break;
                    
                case ACTION_INCREASE_DEPTH:
                    depth.setValue(depth.getValue() * 2);
                    tfDepth.setText(Integer.toString(depth.getValue()));
                    zoom(0);
                    break;
                        
                case ACTION_EDIT_NAME:
                    
                    break;
                    
                case ACTION_SAVE:
                    String name = tfName.getText();
                    if (name.length() <= 0)
                        System.err.println("Need a name!");
                    else if (!name.contains("."))
                        System.err.println("Name needs an extension.");
                    else {
                        try {
                            String subs[] = name.split("\\.");
                            ImageIO.write(Mandelbrot.generate(1920, 1080, x.getValue(), y.getValue(), span.getValue(), depth.getValue()), subs[subs.length - 1], new File(name));
                        } catch (IOException ex) {
                            System.err.println("Failed to save image!");
                        }
                    }
                    break;
                    
                case ACTION_SAVE_COORDS:
                    
                    break;
                    
                case ACTION_LOAD_COORDS:
                    
                    break;
                    
                default:
                    System.err.print("Uknown command: ");
                    System.err.println(cmd);
                    break;
            }
        }
        
    }
    
    static class KeyListener implements ActionListener {

        private MutableDouble refDouble = null;
        private MutableInteger refInteger = null;
        private final TextField textField;
        
        public KeyListener(TextField field, MutableDouble value) {
            this.textField = field;
            this.refDouble = value;
        }
        
        public KeyListener(TextField field, MutableInteger value) {
            this.textField = field;
            this.refInteger = value;
        }
        
        public void connect() {
            this.textField.addActionListener(this);
        }
        
        @Override
        public void actionPerformed(ActionEvent e) {
            if (refDouble != null) {
                refDouble.setValue(Double.valueOf(textField.getText()));
            } else {
                refInteger.setValue(Integer.valueOf(textField.getText()));
            }
        }
        
    }
}
