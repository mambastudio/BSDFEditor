/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Color4_b;
import bsdf.geom.Point3_b;

/**
 *
 * @author user
 */
public class SurfaceParameter_b{
    //this surface is done by texture
    public boolean          isDiffuseTexture;
    public boolean          isGlossyTexture;
    public boolean          isRoughnessTexture;
    public boolean          isMirrorTexture;
    
    //brdf parameters
    public Color4_b          diffuse_color;
    public Point3_b          diffuse_param;     //x = scale
    public Color4_b          glossy_color;
    public Point3_b          glossy_param;      //x = scale, Y = ax, z = ay
    public Color4_b          mirror_color;
    public Point3_b          mirror_param;      //x = scale, Y = ior, when IOR >= 0, we also transmit (just clear glass)
    public Color4_b          emission_color;
    public Point3_b          emission_param;    //x = scale, Y = power
    
    public SurfaceParameter_b()
    {
        isDiffuseTexture = false;
        isGlossyTexture = false;
        isRoughnessTexture = false;
        isMirrorTexture = false;
        
        diffuse_color   = new Color4_b(0.95f, 0.95f, 0.95f);
        diffuse_param   = new Point3_b(1, 0, 0);
        glossy_color    = new Color4_b(0.95f, 0.95f, 0.95f);
        glossy_param    = new Point3_b();
        mirror_color    = new Color4_b(0.95f, 0.95f, 0.95f);
        mirror_param    = new Point3_b();
        emission_color  = new Color4_b(1f, 1f, 1f);
        emission_param  = new Point3_b();
    }
    
    public SurfaceParameter_b copy()
    {
        SurfaceParameter_b param    = new SurfaceParameter_b();
        param.isDiffuseTexture      = isDiffuseTexture;
        param.isGlossyTexture       = isGlossyTexture;
        param.isRoughnessTexture    = isRoughnessTexture;
        param.isMirrorTexture       = isMirrorTexture;
        
        param.diffuse_color         = diffuse_color.copy();
        param.diffuse_param         = diffuse_param.copy(); 
        param.glossy_color          = glossy_color .copy(); 
        param.glossy_param          = glossy_param.copy();  
        param.mirror_color          = mirror_color.copy();  
        param.mirror_param          = mirror_param.copy();  
        param.emission_color        = emission_color.copy();
        param.emission_param        = emission_param.copy();
        
        return param;
    }
}