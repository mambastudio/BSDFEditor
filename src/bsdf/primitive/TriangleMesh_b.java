/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.primitive;

import bsdf.abstracts.Primitive_b;
import bsdf.accelerator.BVH_b;
import bsdf.geom.BBox_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Point2_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Ray_b;
import bsdf.geom.Vector3_b;
import bsdf.surface.Material_b;
import coordinate.generic.AbstractMesh;
import coordinate.generic.raytrace.AbstractAccelerator;
import coordinate.list.CoordinateFloatList;
import coordinate.list.IntList;
import coordinate.parser.attribute.MaterialT;
import coordinate.utility.Value2Df;
import java.util.ArrayList;

/**
 *
 * @author user
 */
public class TriangleMesh_b extends AbstractMesh<Point3_b, Vector3_b, Point2_b> implements Primitive_b
{
    private AbstractAccelerator accelerator;
    private final BBox_b bounds;
    private final ArrayList<Material_b> materialList;
    
    public TriangleMesh_b()
    {
        points = new CoordinateFloatList(Point3_b.class);
        normals = new CoordinateFloatList(Vector3_b.class);
        texcoords = new CoordinateFloatList(Point2_b.class);
        materialList= new ArrayList();
        triangleFaces = new IntList();
        bounds = new BBox_b();
    }
    
    public void initCoordList(int sizeP, int sizeN, int sizeT, int sizeF)
    {
        this.initCoordList(Point3_b.class, Vector3_b.class, Point2_b.class, sizeP, sizeN, sizeT, sizeF);
    }
    
    @Override
    public void setMaterialList(ArrayList<MaterialT> materialTList)
    {
        super.setMaterialList(materialTList);
        
        for(MaterialT matT : materialTList)
        {
            Material_b mat = new Material_b();
            mat.setMaterialT(matT);
            materialList.add(mat);
            
        }
    }
    
    public Vector3_b getNorm(int primID)
    {
        Point3_b p1 = getVertex1(primID);
        Point3_b p2 = getVertex2(primID);
        Point3_b p3 = getVertex3(primID);
        
        Vector3_b e1 = Point3_b.sub(p2, p1);
        Vector3_b e2 = Point3_b.sub(p3, p1);

        return Vector3_b.cross(e1, e2).normalize();
        
    }
    
    public Vector3_b e1(int primID)
    {
        Point3_b p1 = getVertex1(primID);
        Point3_b p2 = getVertex2(primID);
        
        return Point3_b.sub(p2, p1);
        
    }
    
    public Vector3_b e2(int primID)
    {
        Point3_b p1 = getVertex1(primID);
        Point3_b p3 = getVertex3(primID);
        
        return  Point3_b.sub(p3, p1);
    }
    
    
    
    @Override
    public void addPoint(Point3_b p) {
        points.add(p);
        bounds.include(p);
    }

    @Override
    public void addPoint(float... values) {
        Point3_b p = new Point3_b(values[0], values[1], values[2]);
        addPoint(p);
        bounds.include(p);
    }

    @Override
    public void addNormal(Vector3_b n) {
        normals.add(n);
    }

    @Override
    public void addNormal(float... values) {
        Vector3_b n = new Vector3_b(values[0], values[1], values[2]);
        normals.add(n);
    }

    @Override
    public void addTexCoord(Point2_b t) {
        texcoords.add(t);
    }

    @Override
    public void addTexCoord(float... values) {
        Point2_b t = new Point2_b(values[0], values[1]);
        texcoords.add(t);   
    }

    @Override
    public int getSize() {
        return triangleSize();
    }

    @Override
    public BBox_b getBound(int primID) {
        BBox_b bbox = new BBox_b();
        bbox.include(getVertex1(primID));
        bbox.include(getVertex2(primID));
        bbox.include(getVertex3(primID));
        return bbox; 
    }

    @Override
    public Point3_b getCentroid(int primID) {
        return getBound(primID).getCenter();
    }

    @Override
    public BBox_b getBound() {       
        return bounds;
    }

    @Override
    public boolean intersect(Ray_b r, int primID, Isect_b isect) {
        Point3_b p1 = getVertex1(primID);
        Point3_b p2 = getVertex2(primID);
        Point3_b p3 = getVertex3(primID);
        float[] tuv = new float[3];
        
        Face face = this.getFace(primID);
        
        if(mollerIntersection(r, tuv, p1, p2, p3))
        {
            Vector3_b n = getNormal(p1, p2, p3, primID, new Value2Df(tuv[1], tuv[2]));
            n =  n.dot(r.d) > 0.00001f ? n.neg() : n; //normal should always be facing the ray incoming direction
            
            r.setMax(tuv[0]);

            isect.uv.x = tuv[1];
            isect.uv.y = tuv[2];
            isect.n = n;
            isect.p = r.getPoint();
            isect.id = primID;
            isect.primitive = this;
            isect.hit = true;
            isect.mat = this.getMaterial(face.mat);
            
            return true;
        } 
        else
            return false;
    }

    @Override
    public boolean intersectP(Ray_b r, int primID) {
        Point3_b p1 = getVertex1(primID);
        Point3_b p2 = getVertex2(primID);
        Point3_b p3 = getVertex3(primID);
                
        return mollerIntersection(r, null, p1, p2, p3);    
    }

    @Override
    public boolean intersect(Ray_b r, Isect_b isect) {
        return accelerator.intersect(r, isect);
    }

    @Override
    public boolean intersectP(Ray_b r) {
        return accelerator.intersectP(r);
    }

    @Override
    public AbstractAccelerator getAccelerator() {
        return accelerator;
    }

    @Override
    public void buildAccelerator() {
        accelerator = new BVH_b();
        accelerator.build(this);
    }

    @Override
    public float getArea(int primID) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    
    public Vector3_b getNormal(Point3_b p1, Point3_b p2, Point3_b p3, int primID, Value2Df uv)
    {        
        if(hasNormal(primID) && hasUV(primID))
        {
            Vector3_b n1 = getNormal1(primID);
            Vector3_b n2 = getNormal2(primID);
            Vector3_b n3 = getNormal3(primID);
            
            return n1.mul(1 - uv.x - uv.y).add(n2.mul(uv.x).add(n3.mul(uv.y)));            
        }
        else
        {
            Vector3_b e1 = Point3_b.sub(p2, p1);
            Vector3_b e2 = Point3_b.sub(p3, p1);

            return Vector3_b.cross(e1, e2).normalize();
        }
    } 
    
    public boolean mollerIntersection(Ray_b r, float[] tuv, Point3_b p1, Point3_b p2, Point3_b p3)
    {
        Vector3_b e1, e2, h, s, q;
        double a, f, b1, b2;

        e1 = Point3_b.sub(p2, p1);
        e2 = Point3_b.sub(p3, p1);
        h = Vector3_b.cross(r.d, e2);
        a = Vector3_b.dot(e1, h);

        if (a > -0.0000001 && a < 0.0000001)
            return false;

        f = 1/a;
        
        s = Point3_b.sub(r.o, p1);
	b1 = f * (Vector3_b.dot(s, h));

        if (b1 < 0.0 || b1 > 1.0)
            return false;

        q = Vector3_b.cross(s, e1);
	b2 = f * Vector3_b.dot(r.d, q);

	if (b2 < 0.0 || b1 + b2 > 1.0)
            return false;

	float t = (float) (f * Vector3_b.dot(e2, q));
        
        if(r.isInside(t)) 
        {
            if(tuv != null)
            {
                tuv[0] = t;
                tuv[1] = (float) b1;
                tuv[2] = (float) b2;
            }
            return true;
        }
        else
            return false;
    }
    
    public boolean mollerIntersection1(Ray_b r, float[] tuv, Point3_b p1, Point3_b p2, Point3_b p3)
    {    
        int kz = r.getDirection().getMaxDimAbs();
        int kx = kz+1; if (kx == 3) kx = 0;
        int ky = kx+1; if (ky == 3) ky = 0;
        
                
        if(r.getDirection().get(kz)<0.0f)
        {
            int temp = kx;
            kx = ky;
            ky = temp;
        }
        
        float Sx = r.getDirection().get(kx)/r.getDirection().get(kz);
        float Sy = r.getDirection().get(ky)/r.getDirection().get(kz);
        float Sz = 1.f/r.getDirection().get(kz);
        
        Vector3_b A = p1.sub(r.getOrigin());
        Vector3_b B = p2.sub(r.getOrigin());
        Vector3_b C = p3.sub(r.getOrigin());
        
        float Ax = A.get(kx) - Sx*A.get(kz);
        float Ay = A.get(ky) - Sy*A.get(kz);
        float Bx = B.get(kx) - Sx*B.get(kz);
        float By = B.get(ky) - Sy*B.get(kz);
        float Cx = C.get(kx) - Sx*C.get(kz);
        float Cy = C.get(ky) - Sy*C.get(kz);

        float U = Cx*By - Cy*Bx;
        float V = Ax*Cy - Ay*Cx;
        float W = Bx*Ay - By*Ax;
        
        //fallback
        if (U == 0.0f || V == 0.0f || W == 0.0f) {
            double CxBy = (double)Cx*(double)By;
            double CyBx = (double)Cy*(double)Bx;
            U = (float)(CxBy - CyBx);
            double AxCy = (double)Ax*(double)Cy;
            double AyCx = (double)Ay*(double)Cx;
            V = (float)(AxCy - AyCx);
            double BxAy = (double)Bx*(double)Ay;
            double ByAx = (double)By*(double)Ax;
            W = (float)(BxAy - ByAx);
        }
        
        if ((U<0.0f || V<0.0f || W<0.0f) &&
            (U>0.0f || V>0.0f || W>0.0f)) 
            return false;
        
        float det = U+V+W;
        if (det == 0.0f) 
            return false;
        
        float Az = Sz*A.get(kz);
        float Bz = Sz*B.get(kz);
        float Cz = Sz*C.get(kz);
        float T = U*Az + V*Bz + W*Cz;
        
        float rcpDet = 1.0f/det;
        
        float t = T*rcpDet;
        float u = U*rcpDet;
        float v = W*rcpDet;
              
        if(r.isInside(t)) 
        {
            if(tuv != null)
            {
                tuv[0] = t;
                tuv[1] = (float) u;
                tuv[2] = (float) v;
            }
            return true;
        }
        else
            return false;
    }

    @Override
    public Material_b getMaterial(int index) {
        return materialList.get(index);
    }
    
    public Material_b getMaterialFromPrimID(int primID)
    {
        Face face = this.getFace(primID);
        return this.getMaterial(face.mat);
    }
}
