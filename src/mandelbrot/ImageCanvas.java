/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferStrategy;

/**
 *
 * @author Griffone
 */
public class ImageCanvas extends Canvas {
    
    private Image image;
    private int x = 0, y = 0;
    
    private int rx = 0, ry = 0, rw = 0, rh = 0;
    private boolean showRect = false;
    
    public ImageCanvas() {
        super();
        setBackground(Color.BLACK);
    }
    
    public void initializeBuffers() {
        BufferStrategy bs = getBufferStrategy();
        if (bs != null)
            bs.dispose();
        createBufferStrategy(2);
    }
    
    public void setImage(Image image) {
        this.image = image;
        x = y = 0;
        update(null);
    }
    
    public void setRectShown(boolean shown) {
        showRect = shown;
        update(null);
    }
    
    public void updateRect(int x0, int y0, int x1, int y1) {
        rx = Math.min(x0, x1);
        ry = Math.min(y0, y1);
        rw = Math.max(x0, x1) - rx;
        rh = Math.max(y0, y1) - ry;
    }
    
    public void pan(int deltaX, int deltaY) {
        x += deltaX;
        y += deltaY;
        update(null);
    }
    
    @Override
    public void paint(Graphics g) {
        update(null);
    }
    
    @Override
    public void update(Graphics g) {
        BufferStrategy bs = getBufferStrategy();
        g = bs.getDrawGraphics();
        g.clearRect(0, 0, getWidth(), getHeight());
        g.drawImage(image, x, y, null);
        if (showRect)
            g.drawRect(rx, ry, rw, rh);
        bs.show();
    }
}
