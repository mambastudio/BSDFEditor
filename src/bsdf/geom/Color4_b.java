/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import bitmap.spectrum.CoefficientSpectrum;
import bitmap.spectrum.Spectrum;
import java.util.Arrays;
import javafx.scene.paint.Color;

/**
 *
 * @author user
 */
public class Color4_b extends CoefficientSpectrum<Color4_b>{
    
    
    public Color4_b()
    {
        super(3);        
    }
    
    public Color4_b(float a)
    {
        super(new float[]{a, a, a});
    }
    
    public Color4_b(float r, float g, float b)
    {
        super(new float[]{r, g, b});
    }
    
    public Color4_b(double r, double g, double b)
    {
        this((float)r, (float)g, (float)b);
    }
    
    public float r()
    {
        return c[0];
    }
    
    public float g()
    {
        return c[1];
    }
    
    public float b()
    {
        return c[2];
    }
           
    public void setColorFX(Color color)
    {        
        this.setTo(new Color4_b(color.getRed(), color.getGreen(), color.getBlue()));
    }
    
    public Color getColorFX()
    {
        return new Color(r(), g(), b(), 1);
    }

    
    public bitmap.Color getBitmapColor()
    {        
        return new bitmap.Color(r(), g(), b());
    }
    
    @Override
    public float Y() {
        return 0.212671f * r() + 
               0.715160f * g() +
               0.072169f * b();
    }
    
    @Override
    public Color4_b newInstance() {
        try {
            CoefficientSpectrum that = (CoefficientSpectrum) super.clone();           
            that.c = new float[c.length];
            return (Color4_b) that;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public Color4_b copy() {
        try {
            Color4_b that = (Color4_b) super.clone();
            that.c = Arrays.copyOf(c, c.length);
            return that;
        } catch (CloneNotSupportedException ex) {
            throw new InternalError(ex);
        }
    }
    
    @Override
    public String toString()
    {
        return String.format("(%3.2f, %3.2f, %3.2f)", c[0], c[1], c[2]);
    }
}
