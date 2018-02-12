/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mandelbrot;

import java.awt.image.BufferedImage;

/**
 *
 * @author Griffone
 */
public class MandelbrotDrawer implements Runnable {

    private final Mandelbrot mandelbrot;
    private final ImageCanvas canvas;
    private BufferedImage image;
    
    public MandelbrotDrawer(Mandelbrot mandelbrot, ImageCanvas canvas) {
        this.mandelbrot = mandelbrot;
        this.canvas = canvas;
    }
    
    public void updateImage() {
        synchronized(mandelbrot) {
            mandelbrot.notify();
        }
    }
    
    @Override
    public void run() {
        while (true) {
            synchronized(mandelbrot) {
                try {
                    // Calling wait() will block this thread until another thread
                    // calls notify() on the object.
                    mandelbrot.wait();
                } catch (InterruptedException e) {
                }
            }
            canvas.setImage(mandelbrot.generate());
        }
    }
    
}
