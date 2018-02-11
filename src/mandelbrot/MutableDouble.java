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
public class MutableDouble {
    
    private double value;
    
    public MutableDouble(Double value) {
        this.value = value;
    }
    
    public MutableDouble(double value) {
        this.value = value;
    }
    
    public void setValue(Double value) {
        this.value = value;
    }
    
    public void setValue(double value) {
        this.value = value;
    }
    
    public double getValue() {
        return value;
    }
    
    
}
