/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bitmap.Color;
import bsdf.geom.Color4;
import bsdf.geom.Point3;

/**
 *
 * @author user
 */
public class SurfaceParameter{
    //this surface is done by texture
    public boolean          isDiffuseTexture;
    public boolean          isGlossyTexture;
    public boolean          isRoughnessTexture;
    public boolean          isMirrorTexture;
    
    //brdf parameters
    public Color4          diffuse_color;
    public Point3          diffuse_param;
    public Color4          glossy_color;
    public Point3          glossy_param;
    public Color4          mirror_color;
    public Point3          mirror_param;
    public Color4          emission_color;
    public Point3          emission_param;
    
    public SurfaceParameter()
    {
        isDiffuseTexture = false;
        isGlossyTexture = false;
        isRoughnessTexture = false;
        isMirrorTexture = false;
        
        diffuse_color   = new Color4(0.95f, 0.95f, 0.95f);
        diffuse_param   = new Point3(1, 0, 0);
        glossy_color    = new Color4(0.95f, 0.95f, 0.95f);
        glossy_param    = new Point3();
        mirror_color    = new Color4(0.95f, 0.95f, 0.95f);
        mirror_param    = new Point3();
        emission_color  = new Color4(1f, 1f, 1f);
        emission_param  = new Point3();
    }
    
    public SurfaceParameter copy()
    {
        SurfaceParameter param    = new SurfaceParameter();
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