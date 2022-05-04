/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.generic.VCoord;
import coordinate.utility.Utility;

/**
 *
 * @author user
 */
public class Vector3_b implements VCoord<Vector3_b>{
    
    public float x, y, z;
    
    public Vector3_b() {x = 0; y = 0; z = 0;}
    public Vector3_b(float a) {x = a; y = a; z = a;}
    public Vector3_b(float a, float b, float c) {x = a; y = b; z = c;}
    public Vector3_b(Vector3_b a) {x = a.x; y = a.y; z = a.z;}

    public static Vector3_b cross(Vector3_b a, Vector3_b b)
    {
        return a.cross(b);
    }
    
    public static float dot(Vector3_b a, Vector3_b b) {return a.x*b.x + a.y*b.y + a.z*b.z;}
    
    @Override
    public Vector3_b getCoordInstance() {
        return new Vector3_b();
    }

    @Override
    public Vector3_b copy() {
        return new Vector3_b(x, y, z);
    }
    
    public Point3_b asPoint3f()
    {
        return new Point3_b(x, y, z);
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
            default:
                throw new UnsupportedOperationException("Invalid");
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
            default:
                throw new UnsupportedOperationException("Invalid");
        }
    }

    @Override
    public void set(float... values) {
        x = values[0];
        y = values[1];
        z = values[2];
    }
       

    @Override
    public int getSize() {
        return 3;
    }

    @Override
    public float[] getArray() {
        return new float[] {x, y, z};
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
        }
    }
    
    public int getMaxDimAbs()
    {
        float mx = Math.abs(x);
        float my = Math.abs(y);
        float mz = Math.abs(z);
        
        float max = Utility.max(mx, my, mz);
        
        if(max == mx)
            return 0;
        else if(max == my)
            return 1;
        else
            return 2;
    }

    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public final boolean isBad()
    {
        return (Float.isNaN(this.x)) || (Float.isNaN(this.y)) || (Float.isNaN(this.z)) ||
               (Float.isInfinite(this.x)) || (Float.isInfinite(this.y)) || (Float.isInfinite(this.z));
    }
    
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f)", array[0], array[1], array[2]);
    }
}
