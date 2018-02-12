/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

/**
 *
 * @author Griffone
 */
import java.awt.image.BufferedImage;

/**
 * A class for generating imagery of Mandelbrot set
 * 
 * @author Griffone
 */
public class Mandelbrot {
    
    private int width = 0, height = 0, maxDepth = 0;
    private double left = 0, top = 0, x = 0, y = 0, span = 1;
    private boolean invalid = false;
    
    public Mandelbrot() {}
    public Mandelbrot(int width, int height, double x, double y, double span, int maxDepth) {
        this.width = width;
        this.height = height;
        this.x = x;
        this.y = y;
        this.span = span;
        this.maxDepth = maxDepth;
        this.left = x - span / 2.0;
        this.top = y + span * (double) height / (double) width / 2.0;
    }
    
    public synchronized void setSize(int width, int height) {
        // Top left edge remains in the same place
        span *= (double) width / (double) this.width;
        x = left + span / 2.0;
        y = top - span * (double) height / (double) width / 2.0;
        this.width = width;
        this.height = height;
    }
    
    public synchronized double getLeftEdge() {
        return left;
    }
    
    public synchronized double getRightEdge() {
        return left + span;
    }
    
    public synchronized double getTopEdge() {
        return top;
    }
    
    public synchronized double getBottomEdge() {
        return top - span * (double) height / (double) width;
    }
    
    public synchronized void setTarget(double x, double y) {
        this.x = x;
        this.y = y;
        left = x - span / 2.0;
        top = y + span * (double) height / (double) width / 2.0;
    }
    
    public synchronized void moveTarget(int dx, int dy) {
        double rx = (double) dx / (double) width * span;
        double ry = (double) dy / (double) height * span * (double) height / (double) width;
        x -= rx;
        y += ry;
        left -= rx;
        top += ry;
    }
    
    public synchronized double getX() {
        return x;
    }
    
    public synchronized double getY() {
        return y;
    }
    
    public synchronized void setSpan(double span) {
        this.span = span;
        left = x - span / 2.0;
        top = y + span * (double) height / (double) width / 2.0;
    }
    
    public synchronized double getSpan() {
        return span;
    }
    
    public synchronized void setMaxDepth(int maxDepth) {
        this.maxDepth = maxDepth;
    }
    
    public synchronized int getMaxDepth() {
        return maxDepth;
    }
    
    public void invalidate() {
        invalid = true;
    }

    public BufferedImage generate() {
        return generate(width, height, left, top, span, maxDepth);
    }
    
    public BufferedImage generate(int width, int height) {
        return generate(width, height, left, top, span, maxDepth);
    }
    
    public BufferedImage generate(int width, int height, double left, double top, double span, int maxDepth) {
        invalid = false;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        double step = span / width;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                if (invalid)
                    return null;
                double cr = left + step * col;
                double ci = top - step * row;
                
                double zr = 0;
                double zi = 0;
                double zr2;
                double zi2;
                int i = 0;
                
                do {
                    zr2 = zr * zr;
                    zi2 = zi * zi;
                    
                    zi = 2 * zr * zi + ci;
                    zr = zr2 - zi2 + cr;
                } while (zr2 + zi2 < 4.0 && ++i < maxDepth);
                
                if (i < maxDepth)
                    image.setRGB(col, row, ColorMapper.map(i, maxDepth));
                else
                    image.setRGB(col, row, ColorMapper.map(0, maxDepth));
            }
        }
        return image;
    }
}