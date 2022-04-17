/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.abstracts.MaterialInterface;
import coordinate.parser.attribute.MaterialT;

/**
 *
 * @author user
 */
public class Material implements MaterialInterface<Material>{
     //surface
    public SurfaceParameter param; 
       

    public Material()
    {
        param = new SurfaceParameter();
    }
    
    public void setDiffuse(float r, float g, float b)
    {
        param.diffuse_color.set(r, g, b);        
    }
    
    public void setEmitter(float r, float g, float b)
    {
        param.emission_color.set(r, g, b);
        param.emission_param.set('x', 1);
        param.emission_param.set('y', 15);        
    }
    
    @Override
    public void setMaterial(Material mat) {
        param = mat.param.copy();              
    }

    @Override
    public Material copy() {
        Material mat = new Material();
        mat.param = param.copy();
        return mat;
    }
    
    public void setSurfaceParameter(SurfaceParameter param)
    {
        this.param = param;
    }

    @Override
    public void setMaterialT(MaterialT mat) {
        param.diffuse_color.set(mat.diffuse.r, mat.diffuse.g, mat.diffuse.b, mat.diffuse.w);      
    }
}
