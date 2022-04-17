/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Vector3;
import coordinate.generic.AbstractFrame;

/**
 *
 * @author user
 */
public class Frame implements AbstractFrame<Vector3> {
    private Vector3 mX, mY, mZ;
    
    public Frame()
    {
        mX = new Vector3(1,0,0);
        mY = new Vector3(0,1,0);
        mZ = new Vector3(0,0,1);
    }
    
    public Frame(Vector3 n)
    {
        this();
        setFromZ(n);
    }

    @Override
    public void setFromZ(Vector3 z) {
       
        Vector3 tmpZ = mZ = z.normalize();
        Vector3 tmpX = (Math.abs(tmpZ.x) > 0.99f) ? new Vector3(0,1,0) : new Vector3(1,0,0);
        mY = Vector3.cross(tmpZ, tmpX).normalize();
        mX = Vector3.cross(mY, tmpZ);
        
        
        //branchlessONB(z);
    }
    
    //https://jcgt.org/published/0006/01/01/paper-lowres.pdf
    public void branchlessONB(Vector3 n)
    {
        n.normalizeAssign();        
        mZ = n.copy();
        
        
        
        float sign = Math.copySign(1.0f, n.z);
        float a = -1.0f / (sign + n.z);
        float b = n.x * n.y * a;
        
        mX = new Vector3(1.0f + sign * n.x * n.x * a, sign * b, -sign * n.x);
        mY = new Vector3(b, sign + n.y * n.y * a, -n.y);
        
        
    }
    
    public void frisvadONB(Vector3 n)
    {
        n.normalizeAssign();
        mZ = n.copy();
        
        if(n.z < -0.9999999f) // Handle the singularity
        {
            mX = new Vector3( 0.0f, -1.0f, 0.0f);
            mY = new Vector3(-1.0f, 0.0f, 0.0f);
            return;
        }
        float a = 1.0f / (1.0f + n.z);
        float b = -n.x*n.y*a;
        
        mX = new Vector3(1.0f - n.x*n.x*a, b, -n.x);
        mY = new Vector3(b, 1.0f - n.y*n.y*a, -n.y);
    }


    @Override
    public Vector3 toWorld(Vector3 a) {
        return mX.mul(a.x).add(mY.mul(a.y)).add(mZ.mul(a.z));
    }

    @Override
    public Vector3 toLocal(Vector3 a) {
        return new Vector3(Vector3.dot(a, mX), Vector3.dot(a, mY), Vector3.dot(a, mZ));
    }

    @Override
    public Vector3 binormal(){ return mX; }
    @Override
    public Vector3 tangent (){ return mY; }
    @Override
    public Vector3 normal  (){ return mZ; }
    
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
