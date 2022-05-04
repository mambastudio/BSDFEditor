/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.abstracts;

import bsdf.geom.Color4_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Vector3_b;

/**
 *
 * @author user
 */
public interface EnvLight_b extends AbstractLight_b{
    
    default void accumLightGrid(Color4_b color, Vector3_b hitNormal, Point3_b hitPoint, Vector3_b rayDirectionToLight)
    {
        
    }
    
    default void resetLightGrid()
    {
        
    }
    
    default void setSampleFromLightGrid(boolean dosample)
    {
        
    }
    
    default void updateLightGrid()
    {
        
    }
    
    @Override
    default boolean isFinite()
    {
        return false;
    }
    
    @Override
    default boolean isDelta()
    {
        return false;
    }
}
