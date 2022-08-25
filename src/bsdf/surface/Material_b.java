/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import coordinate.parser.attribute.MaterialT;
import bsdf.abstracts.MaterialInterface_b;

/**
 *
 * @author user
 */
public class Material_b implements MaterialInterface_b<Material_b>{
     //surface
    public SurfaceParameter_b param; 
    public String name;   

    public Material_b()
    {
        param = new SurfaceParameter_b();
    }
    
    public void setDiffuse(float r, float g, float b)
    {
        param.diffuse_color.setTo(r, g, b);        
    }
    
    public void setEmitter(float r, float g, float b)
    {
        param.emission_color.setTo(r, g, b);
        param.emission_param.set('x', 1);
        param.emission_param.set('y', 15);        
    }
    
    public void enableEmitter()
    {
        param.emission_param.set('x', 1);
        param.emission_param.set('y', 15);        
    }
    
    public void disableEmitter()
    {
        param.emission_param.set('x', 0);
        param.emission_param.set('y', 0);        
    }
    
    public boolean isEmitter()
    {
        return param.emission_param.get('x') == 1;       
    }
    
    @Override
    public void setMaterial(Material_b mat) {
        param = mat.param.copy();              
    }

    @Override
    public Material_b copy() {
        Material_b mat = new Material_b();
        mat.param = param.copy();
        return mat;
    }
    
    public void setSurfaceParameter(SurfaceParameter_b param)
    {
        this.param = param;
    }

    @Override
    public void setMaterialT(MaterialT mat) {
        param.diffuse_color.setTo(mat.diffuse.r, mat.diffuse.g, mat.diffuse.b);  
        param.diffuse_param.set('x', mat.diffuseWeight);
                
        param.emission_color.setTo(mat.emitter.r, mat.emitter.g, mat.emitter.b);  
        param.emission_param.set('x', mat.emitterEnabled ? 1 : 0);
        param.emission_param.set('y', mat.emitter.w);
        
        name = mat.getNameString();
    }
    
    public void setName(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }
    
    @Override
    public String toString()
    {
        return name;
    }
}
