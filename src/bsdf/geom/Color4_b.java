/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import javafx.scene.paint.Color;

/**
 *
 * @author user
 */
public class Color4_b {
    public float r, g, b, w;
    
    public Color4_b()
    {
        r = g = b = 0;
        w = 1;
    }
    
    public Color4_b(float r, float g, float b)
    {
        this.r = r; this.g = g; this.b = b;
        this.w = 1;
    }
    
    public Color4_b(float r, float g, float b, float w)
    {
        this(r, g, b);
        this.w = w;
    }
    
    public final boolean isBlack()
    {
        return r <= 0 && g <= 0 && b <= 0;
    }
    
    public final void addAssign(Color4_b c)
    {
        r += c.r;
        g += c.g;
        b += c.b;
    }
    
    public final Color4_b mul(float s)
    {
        return new Color4_b(r * s,
                          g * s,
                          b * s);
    }
    
    public final Color4_b mul(Color4_b s)
    {
        return new Color4_b(r * s.r,
                          g * s.g,
                          b * s.b);
    }
    
    public float luminance()
    {
        return 0.212671f * r + 
               0.715160f * g +
               0.072169f * b;
    }
    
    public void setColorFX(Color color)
    {
        set(color.getRed(), color.getGreen(), color.getBlue());
    }
    
    public void set(float r, float g, float b)
    {
        this.r = r; this.g = g; this.b = b;
    }
    
    public void set(float r, float g, float b, float w)
    {
        this.r = r; this.g = g; this.b = b; this.w = w;
    }
    
    public void set(double r, double g, double b)
    {
        this.r = (float) r; this.g = (float) g; this.b = (float) b;
    }
    
    public Color getColorFX()
    {
        return new Color(r, g, b, w);
    }

    public Color4_b copy()
    {
        return new Color4_b(r, g, b, w);
    }
    
    private float[] getArray()
    {
        return new float[]{r, g, b, w};
    }
    
    public bitmap.Color getBitmapColor()
    {        
        return new bitmap.Color(r, g, b);
    }
    
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f, %3.2f)", array[0], array[1], array[2], array[3]);
    }
}
