/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.generic.AbstractRay;

/**
 *
 * @author user
 */
public class Ray implements AbstractRay<Point3, Vector3>
{
    public Point3 o = null;
    public Vector3 d = null;
    
    public Vector3 inv_d = null;    
    private float tMin;
    private float tMax;
    
    public int[] sign;
    
    public static final float EPSILON = 0.00001f;// 0.01f;
    
    public Ray() 
    {
        o = new Point3();
        d = new Vector3();  
        
        tMin = EPSILON;
        tMax = Float.POSITIVE_INFINITY;        
    }
    
    public final void init()
    {        
        inv_d = new Vector3(1f/d.x, 1f/d.y, 1f/d.z);
        sign = new int[3];
        sign[0] = inv_d.x < 0 ? 1 : 0;
        sign[1] = inv_d.y < 0 ? 1 : 0;
        sign[2] = inv_d.z < 0 ? 1 : 0;
    }
    
    public int[] dirIsNeg()
    {
        int[] dirIsNeg = {sign[0], sign[1], sign[2]};
        return dirIsNeg;
    }
    
    public final boolean isInside(float t) 
    {
        return (tMin < t) && (t < tMax);
    }
    
    public Vector3 getInvDir()
    {
        return new Vector3(inv_d);
    }
    
    @Override
    public void set(float ox, float oy, float oz, float dx, float dy, float dz) {
        o = new Point3(ox, oy, oz);
        d = new Vector3(dx, dy, dz).normalize();  
        
        tMin = EPSILON;
        tMax = Float.POSITIVE_INFINITY;
        
        init();
    }

    @Override
    public void set(Point3 o, Vector3 d) {
        set(o.x, o.y, o.z, d.x, d.y, d.z);
    }
    
    public void set(Point3 o, Vector3 d, float tMax) {
        set(o.x, o.y, o.z, d.x, d.y, d.z);
        this.tMax = tMax;
    }

    @Override
    public Point3 getPoint() {
        Point3 dest = new Point3();        
        dest.x = o.x + (tMax * d.x);
        dest.y = o.y + (tMax * d.y);
        dest.z = o.z + (tMax * d.z);
        return dest;
    }

    @Override
    public Point3 getPoint(float t) {
        Point3 dest = new Point3();        
        dest.x = o.x + (t * d.x);
        dest.y = o.y + (t * d.y);
        dest.z = o.z + (t * d.z);
        return dest;
    }

    @Override
    public Vector3 getDirection() {
        return d;
    }
        
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        
        builder.append("Ray: ").append("\n");
        builder.append("         o    ").append(String.format("(%.5f, %.5f, %.5f)", o.x, o.y, o.z)).append("\n");
        builder.append("         d    ").append(String.format("(%.5f, %.5f, %.5f)", d.x, d.y, d.z)).append("\n");
        builder.append("         tMin ").append(String.format("(%.5f)", tMin)).append("\n");
        builder.append("         tMax ").append(String.format("(%.5f)", tMax));
                
        return builder.toString();   
    }

    @Override
    public float getMin() {
        return tMin;
    }

    @Override
    public float getMax() {
        return tMax;
    }
    
    public final void setMax(float t) {
        tMax = t;
    }
    
    @Override
    public Point3 getOrigin() {
        return o.copy();
    }

    @Override
    public Vector3 getInverseDirection() {
        return inv_d.copy();
    }

    @Override
    public Ray copy() {
        Ray ray = new Ray();
        ray.set(o, d);
        ray.setMax(getMax());
        return ray;
    }
}
