/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.generic.SCoord;

/**
 *
 * @author user
 */
public class Point3 implements SCoord<Point3, Vector3> {

    public float x, y, z;
    
    public Point3()
    {
        
    }
    
    public Point3(float v)
    {
        x = y = z = v;
    }
    
    public Point3(float x, float y, float z)
    {
        this.x = x; this.y = y; this.z = z;
    }
    
    public Point3(Point3 p)
    {
        this(p.x, p.y, p.z);
    }
    
    public boolean isZero()
    {
        return x == 0 && y == 0 && z == 0;
    }
    
    public static final Vector3 sub(Point3 p1, Point3 p2) 
    {
        Vector3 dest = new Vector3();
        dest.x = p1.x - p2.x;
        dest.y = p1.y - p2.y;
        dest.z = p1.z - p2.z;
        return dest;
    }
    
    public final Vector3 asVector3f()
    {
        return new Vector3(x, y, z);
    }
    
    @Override
    public Point3 getSCoordInstance() {
        return new Point3();
    }

    @Override
    public Vector3 getVCoordInstance() {
        return new Vector3();
    }

    @Override
    public Point3 copy() {
        return new Point3(x, y, z);
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
                throw new UnsupportedOperationException("Not supported yet."); 
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
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

    @Override
    public int getByteSize() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public String toString()
    {
        float[] array = getArray();
        return String.format("(%3.2f, %3.2f, %3.2f)", array[0], array[1], array[2]);
    }
}
