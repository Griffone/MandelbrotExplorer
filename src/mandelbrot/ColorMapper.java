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
public class ColorMapper {
    
    public static int map(int depth, int maxDepth) {
        return red(depth, maxDepth);
    }
    
    public static int greyscale(int depth, int maxDepth) {
        double rDepth = (double) depth / (double) maxDepth;
        int r = (int) Math.floor(0xFF * rDepth);
        return 0xFF000000 | r << 16 | r << 8 | r;
    }
    
    public static int red(int depth, int maxDepth) {
        double rDepth = (double) depth / (double) maxDepth;
        int a = (int) Math.floor(rDepth * 4);
        int x = (int) Math.floor(255 * (rDepth * 4 - (double) a));
        switch (a) {
            case 0:
                return 0xFF000000 | x << 16;
            case 1:
                return 0xFFFF0000 | x << 8;
            case 2:
                return 0xFF00FF00 | (0xFF - x) << 16;
            case 3:
                return 0xFF00FF00 | x;
            default:
                return 0xFF0000FF | (0xFF - x) << 8;
        }
    }
}
