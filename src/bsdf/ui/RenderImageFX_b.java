/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.ui;

import bitmap.Color;
import bitmap.RGBSpace;
import bitmap.XYZ;
import bitmap.core.BitmapInterface;
import bitmap.image.BitmapRGB;
import coordinate.utility.Utility;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import javafx.scene.image.Image;

/**
 *
 * @author user
 */
public class RenderImageFX_b  implements BitmapInterface {   
    private float gamma = 2.2f;
    private float exposure = 0.18f;
    private final int width;
    private final int height;
    
    private final Color[] accum;    
    private final BitmapRGB bitmap;
        
    private float count = 0;
    
    private float logwTotal = 0;
    private float logwCount = 0;
    private float logAverageLum = 0;
    
    public RenderImageFX_b(int width, int height)
    {
        this.width = width;
        this.height = height;
        this.accum = new Color[width * height];
        this.bitmap = new BitmapRGB(width, height);
        
        for(int i = 0; i<(width * height); i++)
        {
            accum[i] = new Color();            
        }
    }
    private void initAllLogW()
    {
        logAverageLum = 0;
        initLogW();
    }
    private void initLogW()
    {
        logwTotal = 0;
        logwCount = 0;
    }
    
    public void addAccum(Color color, int index)
    {
        accum[index].addAssign(color);
    }
    
    private int[] pixel(int index)
    {
        int[] pixel = new int[2];
        pixel[0] = index % width;
        pixel[1] = index / height;
        return pixel;
    }
    
    public void update()
    {
        
        
        //from previous calculations
        if(logwCount > 0)
        {
            logAverageLum = logwTotal/logwCount;
            logAverageLum = (float) exp(logAverageLum);
        }
        
        this.initLogW();
        
        if(count > 0)
            for(int i = 0; i<accum.length; i++)
            {
                int[] pixel = pixel(i);
                int x = pixel[0];
                int y = pixel[1];

                Color color = accum[i].copy();
                color.divAssign(count());
                
                float lum = color.luminance();
                if(lum>0)
                {
                    logwTotal += (float)log(0.01f + color.luminance());
                    logwCount += 1;
                    
                    if(logAverageLum > 0)
                    {
                        float scaledLuminance = exposure * lum/logAverageLum;                        
                        float Y = toneSimpleReinhard(scaledLuminance);
                        Y = Utility.check(Y);
                        XYZ colorXYZ = RGBSpace.convertRGBtoXYZ(color);
                        colorXYZ.xyz();
                        colorXYZ.Y = Y;                        
                        colorXYZ.xyYtoXYZ();
                        color = RGBSpace.convertXYZtoRGB(colorXYZ);
                        //color.mulAssign(scaledLuminance);
                    }
                }
                
                color = color.simpleGamma(gamma);
                
                bitmap.writeColor(color, 1, x, y);
                
                //System.out.println(rgb.readColor(x, y));
            }
    }
    
    private float toneSimpleReinhard(float Y)
    {
       return Y / (1.f + Y);
    }
    
    @Override
    public Image getImage()
    {
        return bitmap.getImage();
    }
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }
    
    public Image getBlackImage()
    {
        BitmapRGB blackImage = new BitmapRGB(width, height);
        return blackImage.getImage();
    }
    
    public float count()
    {
        return count;
    }
    
    public void incrementCount()
    {
        count++;
    }
    
    public void setGamma(float value)
    {
        this.gamma = value;
    }
    
    public float getGamma()
    {
        return gamma;
    }
    
    public void setExposure(float value)
    {
        this.exposure = value;
    }
    
    public float getExposure()
    {
        return exposure;
    }
    
    @Override
    public Color readColor(int x, int y){
        return bitmap.readColor(x, y);
    }
    
    @Override
    public float readAlpha(int x, int y){
        return bitmap.readAlpha(x, y);
    }
   
    @Override
    public void clear(){
        count = 0;
        initAllLogW();        
        for(int i = 0; i<accum.length; i++)
        {
            accum[i] = new Color();
            int pixel[] = pixel(i);
            bitmap.writeColor(new Color(), gamma, pixel[0], pixel[1]);
        }
    }
}
