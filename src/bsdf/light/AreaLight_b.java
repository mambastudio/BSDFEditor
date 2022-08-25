/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.light;

import bsdf.abstracts.AbstractLight_b;
import bsdf.geom.Color4_b;
import bsdf.geom.Point3_b;
import bsdf.geom.SceneSphere_b;
import bsdf.geom.Vector3_b;
import bsdf.primitive.TriangleMesh_b;
import bsdf.surface.Frame_b;
import bsdf.surface.Material_b;
import coordinate.utility.Utility;
import static coordinate.utility.Utility.EPS_COSINE;
import static coordinate.utility.Utility.INV_PI_F;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;

/**
 *
 * @author user
 */
public class AreaLight_b implements AbstractLight_b
{
    private int primID = -1;
    private TriangleMesh_b mesh = null;
    
    private Point3_b p0;
    private Vector3_b e1, e2;
    private Frame_b mFrame;
    private Color4_b mIntensity;
    private float mInvArea = 0;
    
    public AreaLight_b(TriangleMesh_b mesh, int primID)
    {
        Material_b mat = mesh.getMaterialFromPrimID(primID);
        if(!mat.isEmitter())
            throw new UnsupportedOperationException("this is not a light");
        else
        {
            this.primID = primID;
            this.mesh = mesh;
            
            this.init();
        }
    }
    
    private void init()
    {        
        e1 = mesh.getVertex2(primID).sub(mesh.getVertex1(primID));
        e2 = mesh.getVertex3(primID).sub(mesh.getVertex1(primID));
        p0 = mesh.getVertex1(primID);
        
        Vector3_b normal = e1.cross(e2);
        float len    = normal.length();
        mInvArea     = 2.f / len;
        
        mFrame = new Frame_b();
        mFrame.setFromZ(normal);
        
        mIntensity = new Color4_b();
        Material_b mat = mesh.getMaterialFromPrimID(primID);
        Color4_b col = (Color4_b) mat.param.emission_color.copy();
        col.mul(mat.param.emission_param.y);
        mIntensity.setTo(col);
    }

    @Override
    public Color4_b illuminate(
            SceneSphere_b aSceneSphere, 
            Point3_b aReceivingPosition, 
            Vector3_b aNormal, 
            Value2Df aRndTuple, 
            Vector3_b oDirectionToLight, 
            Value1Df oDistance, 
            Value1Df oDirectPdfW, 
            Value1Df oEmissionPdfW, 
            Value1Df oCosAtLight) 
    {
        Value2Df uv = Utility.sampleUniformTriangle(aRndTuple);
        Point3_b lightPoint = p0.add(e1.mul(uv.x).add(e2.mul(uv.y)));
        
        oDirectionToLight.setValue(lightPoint.sub(aReceivingPosition));
        float distSqr   = oDirectionToLight.lenSqr();
        oDistance.x             = (float) Math.sqrt(distSqr);
        oDirectionToLight.setValue(oDirectionToLight.div(oDistance.x));

        float cosNormalDir = Vector3_b.dot(mFrame.normal(), oDirectionToLight.neg());

        // too close to, or under, tangent
        if(cosNormalDir < EPS_COSINE)
        {
            return new Color4_b();
        }

        oDirectPdfW.x = mInvArea * distSqr / cosNormalDir;

        if(oCosAtLight != null)
            oCosAtLight.x = cosNormalDir;

        if(oEmissionPdfW != null)
            oEmissionPdfW.x = mInvArea * cosNormalDir * INV_PI_F;

        return mIntensity;
    }

    @Override
    public Color4_b emit(SceneSphere_b aSceneSphere, Value2Df aDirRndTuple, Value2Df aPosRndTuple, Point3_b oPosition, Vector3_b oDirection, Value1Df oEmissionPdfW, Value1Df oDirectPdfA, Value1Df oCosThetaLight) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Color4_b getRadiance(SceneSphere_b aSceneSphere, Vector3_b aRayDirection, Point3_b aHitPoint, Value1Df oDirectPdfA, Value1Df oEmissionPdfW) {
        float cosOutL = Math.max(0.f, Vector3_b.dot(mFrame.normal(), aRayDirection.neg()));

        if(cosOutL == 0)
            return new Color4_b();

        if(oDirectPdfA != null)
            oDirectPdfA.x = mInvArea;

        if(oEmissionPdfW != null)
        {
            oEmissionPdfW.x = Utility.cosHemispherePdfW(mFrame.normal(), aRayDirection.neg());
            oEmissionPdfW.x *= mInvArea;
        }

        return mIntensity;
    }

    @Override
    public boolean isFinite() {
        return true;
    }

    @Override
    public boolean isDelta() {
        return false;
    }
    
    
    
}
