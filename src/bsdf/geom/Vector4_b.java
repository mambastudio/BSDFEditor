/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.generic.VCoord;

/**
 *
 * @author user
 */
public class Vector4_b implements VCoord{
    
    public float x, y, z, w;
    public Vector4_b(){}
    public Vector4_b(float x, float y, float z, float w){this.x = x; this.y = y; this.z = z; this.w = w;};
    public Vector4_b(Vector4_b v) {this.x = v.x; this.y = v.y; this.z = v.z; this.w = v.w;}

    @Override
    public VCoord getCoordInstance() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public VCoord copy() {
        return new Vector4_b(x, y, z, w);
    }

    @Override
    public void set(float... values) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float[] getArray() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
}
