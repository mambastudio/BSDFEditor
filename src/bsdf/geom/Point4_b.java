/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.generic.SCoord;
import coordinate.struct.structfloat.FloatStruct;

/**
 *
 * @author user
 */
public class Point4_b extends FloatStruct implements SCoord<Point4_b, Vector4_b>{
    public float x, y, z, w;
    
    public Point4_b(){
        super();
    }

    public Point4_b(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Point4_b(Point4_b p) {
        x = p.x;
        y = p.y;
        z = p.z;
        z = p.w;
    }
                
    
       
    public Point4_b setValue(float x, float y, float z, float w) {
        Point4_b p = SCoord.super.setValue(x, y, z);
        this.refreshGlobalArray();
        return p;
    }
    

    @Override
    public int getSize() {
        return 4;
    }

    @Override
    public float[] getArray() {
        return new float[]{x, y, z, w};
    }

    @Override
    public void set(float... values) {
        x = values[0];
        y = values[1];
        z = values[2];
        w = values[3];
    }

    @Override
    public float get(char axis) {
        switch (axis) {
            case 'x':
                return x;
            case 'y':
                return y;
            case 'z':
                return z;
            case 'w':
                return w;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.  
        }
    }

    @Override
    public void set(char axis, float value) {
        switch (axis) {
            case 'x':
                x = value;
                break;
            case 'y':
                y = value;
                break;
            case 'z':
                z = value;
                break;
            case 'w':
                w = value;
                break;
            default:
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }
    }

    @Override
    public Point4_b copy() {
        return new Point4_b(x, y, z, w);
    }

    @Override
    public Point4_b getSCoordInstance() {
        return new Point4_b();
    }

    @Override
    public Vector4_b getVCoordInstance() {
        return new Vector4_b();
    }
    
    @Override
    public void setIndex(int index, float value) {
        switch (index)
        {
            case 0:
                x = value;
                break;
            case 1:
                y = value;
                break;    
            case 2:
                z = value;
                break;
            case 3:
                w = value;
                break;
        }
    }
        
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f, %3.2f)", array[0], array[1], array[2], array[3]);
    }

    @Override
    public int getByteSize() {
        return 4;
    }
}
