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
 *
 * @author Griffone
 */
public class Mandelbrot {
    
    public static BufferedImage generate(int width, int height, double x, double y, double span, int maxDepth) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        x = x - span / 2.0;
        y = y + (span * ((double) height / (double)width)) / 2.0;
        double step = span / width;
        
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                double cr = x + (step * col);
                double ci = y - (step * row);
                
                double zr = 0.0;
                double zi = 0.0;
                double zr2;
                double zi2;
                
                int iteration = 0;
                do {
                    zr2 = zr * zr;
                    zi2 = zi * zi;
                    
                    zi = 2 * zr * zi + ci;
                    zr = zr2 - zi2 + cr;
                } while (zr2+zi2 < 4.0 && ++iteration < maxDepth);
                
                if (iteration < maxDepth)
                    image.setRGB(col, row, ColorMapper.map(iteration, maxDepth));
                else
                    image.setRGB(col, row, ColorMapper.map(0, maxDepth));
            }
        }
        
        return image;
    }
}