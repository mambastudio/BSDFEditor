/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Vector3_b;
import coordinate.surface.AbstractFrame;

/**
 *
 * @author user
 */
public class Frame_b implements AbstractFrame<Vector3_b> {
    private Vector3_b mX, mY, mZ;
    
    public Frame_b()
    {
        mX = new Vector3_b(1,0,0);
        mY = new Vector3_b(0,1,0);
        mZ = new Vector3_b(0,0,1);
    }
    
    public Frame_b(Vector3_b n)
    {
        this();
        setFromZ(n);
    }

    @Override
    public void setFromZ(Vector3_b z) {
       
        Vector3_b tmpZ = mZ = z.normalize();
        Vector3_b tmpX = (Math.abs(tmpZ.x) > 0.99f) ? new Vector3_b(0,1,0) : new Vector3_b(1,0,0);
        mY = Vector3_b.cross(tmpZ, tmpX).normalize();
        mX = Vector3_b.cross(mY, tmpZ);
        
        
        //branchlessONB(z);
    }
    
    //https://jcgt.org/published/0006/01/01/paper-lowres.pdf
    public void branchlessONB(Vector3_b n)
    {
        n.normalizeAssign();        
        mZ = n.copy();
        
        
        
        float sign = Math.copySign(1.0f, n.z);
        float a = -1.0f / (sign + n.z);
        float b = n.x * n.y * a;
        
        mX = new Vector3_b(1.0f + sign * n.x * n.x * a, sign * b, -sign * n.x);
        mY = new Vector3_b(b, sign + n.y * n.y * a, -n.y);
        
        
    }
    
    public void frisvadONB(Vector3_b n)
    {
        n.normalizeAssign();
        mZ = n.copy();
        
        if(n.z < -0.9999999f) // Handle the singularity
        {
            mX = new Vector3_b( 0.0f, -1.0f, 0.0f);
            mY = new Vector3_b(-1.0f, 0.0f, 0.0f);
            return;
        }
        float a = 1.0f / (1.0f + n.z);
        float b = -n.x*n.y*a;
        
        mX = new Vector3_b(1.0f - n.x*n.x*a, b, -n.x);
        mY = new Vector3_b(b, 1.0f - n.y*n.y*a, -n.y);
    }


    @Override
    public Vector3_b toWorld(Vector3_b a) {
        return mX.mul(a.x).add(mY.mul(a.y)).add(mZ.mul(a.z));
    }

    @Override
    public Vector3_b toLocal(Vector3_b a) {
        return new Vector3_b(Vector3_b.dot(a, mX), Vector3_b.dot(a, mY), Vector3_b.dot(a, mZ));
    }

    @Override
    public Vector3_b binormal(){ return mX; }
    @Override
    public Vector3_b tangent (){ return mY; }
    @Override
    public Vector3_b normal  (){ return mZ; }
    
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("Frame").append("\n");
        builder.append(mX).append("\n");
        builder.append(mY).append("\n");
        builder.append(mZ).append("\n");
        return builder.toString();
    }
}
