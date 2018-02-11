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
public class MutableInteger {
    
    private int value;
    
    public MutableInteger(int value) {
        this.value = value;
    }
    
    public MutableInteger(Integer value) {
        this.value = value;
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public void setValue(Integer value) {
        this.value = value;
    }
    
    public int getValue() {
        return value;
    }
}
