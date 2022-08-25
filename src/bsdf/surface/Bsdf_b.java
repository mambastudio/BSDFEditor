/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Color4_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Ray_b;
import static bsdf.geom.Util_b.SampleCosHemisphereW;
import bsdf.geom.Vector3_b;
import static coordinate.utility.Utility.EPS_COSINE;
import static coordinate.utility.Utility.INV_PI_F;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;
import coordinate.utility.Value3Df;
import static java.lang.Float.max;
import static java.lang.Math.abs;
import static bsdf.surface.Bsdf_b.Events.kDiffuse;
import static bsdf.surface.Bsdf_b.Events.kGlossy;
import coordinate.surface.Frame;
import static java.lang.Math.PI;
import static java.lang.Math.cos;
import static java.lang.Math.pow;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

/**
 *
 * @author user
 */
public class Bsdf_b {
    
    public enum Events
    {
        kNONE(0),
        kDiffuse(1),
        kGlossy(2),
        kReflect(4),
        kRefract(8),
        kSpecular(kReflect.value, kRefract.value),
        kNonSpecular(kDiffuse.value, kGlossy.value),
        kAll(kSpecular.value, kNonSpecular.value);

        public int value;
        
        Events(int value)
        {
            this.value = value;
        }
        
        Events(int value1, int value2)
        {
            value = value1 | value2;
        }
    }
    
    public SurfaceParameter_b  param = null;                 //chosen surface
    public Frame_b frame = null;                    //local frame of reference

    public Vector3_b localDirFix = null;                      //incoming (fixed) incoming direction, in local
    public Vector3_b localGeomN = null;                       //geometry normal (without normal shading) 
    
    public ComponentProbabilities_b probabilities = null;    //sampling probabilities
    
    public Material_b material = null;
    
    private Bsdf_b()
    {
        
    }
    
    
    public static Bsdf_b setupBsdf(Ray_b ray, Isect_b isect)
    {
        Bsdf_b bsdf = new Bsdf_b();
        
        if(isect.hit)
        {            
            //frame for local surface
            bsdf.frame = new Frame_b(isect.n);
                       
            //set local dir fix for ray incoming
            bsdf.localDirFix = bsdf.frame.toLocal(ray.d.neg());
            
            //is bsdf valid
            if(Math.abs(bsdf.localDirFix.z) < EPS_COSINE)
            {
                bsdf.material = null;
                return bsdf;
            }
            
            bsdf.localGeomN = bsdf.frame.toLocal(isect.n);
   
            //set material id
            bsdf.material  = isect.mat;
   
            //choose layer parameter
            bsdf.param = isect.mat.param;
   
            //get probabilities for selecting type of brdf (except emitter)
            bsdf.probabilities = new ComponentProbabilities_b();            
            getComponentProbabilities(bsdf, bsdf.probabilities);            
        }
        return bsdf;
    }
    
    public boolean isValid()
    {
        return material != null;
    }
    
    //TODO
    public boolean isDelta()
    {
        return false;
    }
    
    public float cosThetaFix() 
    { 
        return localDirFix.z; 
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Albedo methods
    ////////////////////////////////////////////////////////////////////////////

    static float AlbedoDiffuse(Bsdf_b bsdf)
    {
       Color4_b color = bsdf.param.diffuse_color.mul(bsdf.param.diffuse_param.x);
       return color.luminance();
    }

    static float AlbedoGlossy(Bsdf_b bsdf)
    {
       Color4_b color = bsdf.param.glossy_color.mul(bsdf.param.glossy_param.x);
       return color.luminance();
    }

    static float AlbedoReflect(Bsdf_b bsdf)
    {
       Color4_b color = bsdf.param.mirror_color.mul(bsdf.param.mirror_param.x);
       return color.luminance();
    }

    static float AlbedoRefract(Bsdf_b bsdf)
    {
       return 0;
    }
    
    static  void getComponentProbabilities(Bsdf_b bsdf, ComponentProbabilities_b probabilities)
    {
       float albedoDiffuse = AlbedoDiffuse(bsdf);
       float albedoGlossy  = AlbedoGlossy(bsdf);
       //float albedoReflect = AlbedoReflect(bsdf);
       //float albedoReflect = mReflectCoeff         * AlbedoReflect(aMaterial);
       //float albedoRefract = (1.f - mReflectCoeff) * AlbedoRefract(aMaterial);

       float totalAlbedo   = albedoDiffuse + albedoGlossy;// + albedoReflect + albedoRefract;

       if(totalAlbedo < 1e-9f)
       {
          probabilities.diffProb  = 0.f;
          probabilities.glossyProb = 0.f;
          //probabilities.reflProb  = 0.f;
          //probabilities.refrProb  = 0.f;
          //mContinuationProb = 0.f;
       }
       else
       {
          probabilities.diffProb   = albedoDiffuse / totalAlbedo;
          probabilities.glossyProb = albedoGlossy  / totalAlbedo;
          //probabilities.reflProb  = albedoReflect / totalAlbedo;
          //probabilities.refrProb  = albedoRefract / totalAlbedo;
          // The continuation probability is max component from reflectance.
          // That way the weight of sample will never rise.
          // Luminance is another very valid option.
          //mContinuationProb =
          //    (aMaterial.mDiffuseReflectance +
          //    aMaterial.mPhongReflectance +
          //    mReflectCoeff * aMaterial.mMirrorReflectance).Max() +
           //   (1.f - mReflectCoeff);

          //mContinuationProb = std::min(1.f, std::max(0.f, mContinuationProb));
       }
    }
    
    public Color4_b SampleDiffuse(       
        Value2Df       sample,
        Vector3_b        localDirGen,
        Value1Df       pdfW)
    {
        if(localDirFix.z < EPS_COSINE)
          return new Color4_b();

        Value1Df unweightedPdfW = new Value1Df();
        localDirGen.setValue(SampleCosHemisphereW(sample, unweightedPdfW));
        pdfW.x += unweightedPdfW.x * probabilities.diffProb;

        return param.diffuse_color.mul(INV_PI_F); 
    }
    
    public Color4_b SampleGlossy(        
        Value2Df       sample,
        Vector3_b      localDirGen,
        Value1Df       pdfW)
    {
        if(localDirFix.z < EPS_COSINE)
          return new Color4_b();
        
        float ax      = param.glossy_param.y;
        float ay      = param.glossy_param.z;
        
        Vector3_b wh     = sampleGGXVNDF(localDirFix, ax, ay, sample.x, sample.y);
        localDirGen.setValue(reflectFromN(localDirFix, wh));
        
        Vector3_b wi = localDirFix.copy();
        Vector3_b wo = localDirGen.copy();

        float cosThetaI = abs(localDirFix.z);
        float cosThetaO = abs(localDirGen.z);
        
        pdfW.x  += probabilities.glossyProb * pdfGGXVNDF(wo, wh, ax, ay);   //ggxPdfReflect(alpha_, wo, (float4)(0, 0, 1, 0), wh);
        
        //System.out.println(pdfW.x);
        
        float fr      = G_Atten(wi, wo, ax, ay) * D_H(wh, ax, ay)/ (4 * cosThetaI * cosThetaO);               //G_Atten(wi, wo, ax, ay)
        
        float whdot =  abs(wh.dot(wo));

        Color4_b Fc     =   SchlickColor(whdot, param.glossy_color);
    
        if(cosThetaI == 0.f || cosThetaO == 0.f)
            Fc = new Color4_b();
        if(wh.x == 0 && wh.y == 0 && wh.z == 0)
            Fc = new Color4_b();

         return Fc.mul(fr);
    }
    
    public Color4_b EvaluateDiffuse(        
        Vector3_b        localDirGen,
        Value1Df       directPdfW)
    {
        if(probabilities.diffProb == 0)
          return new Color4_b();

        if(localDirFix.z < EPS_COSINE || localDirGen.z < EPS_COSINE)
          return new Color4_b();

        if(directPdfW != null)
            directPdfW.x += probabilities.diffProb * max(0.f, localDirGen.z * INV_PI_F);

       return param.diffuse_color.mul(INV_PI_F); 
    }
    
    public Color4_b EvaluateGlossy(        
        Vector3_b        localDirGen,
        Value1Df       directPdfW)
    {
        if(probabilities.glossyProb == 0)
            return new Color4_b();

      if(localDirFix.z < EPS_COSINE || localDirGen.z < EPS_COSINE)
            return new Color4_b();

       float ax      = param.glossy_param.y;
       float ay      = param.glossy_param.z;

       Vector3_b wi     = localDirFix;
       Vector3_b wo     = localDirGen;

       Vector3_b wh     = halfVector(wi, wo);

       float cosThetaI = abs(wi.z);
       float cosThetaO = abs(wo.z);

       directPdfW.x   += probabilities.glossyProb * pdfGGXVNDF(wo, wh, ax, ay);   //ggxPdfReflect(alpha_, wo, (float4)(0, 0, 1, 0), wh);

       float fr      = G_Atten(wi, wo, ax, ay) * D_H(wh, ax, ay)/ (4 * cosThetaI * cosThetaO);               //G_Atten(wi, wo, ax, ay)

       float whdot = abs(wh.dot(wo));

       Color4_b Fc     =  SchlickColor(whdot, param.glossy_color);

       if(cosThetaI == 0.f || cosThetaO == 0.f)
          Fc = new Color4_b();
       if(wh.x == 0 && wh.y == 0 && wh.z == 0)
          Fc = new Color4_b();

       return Fc.mul(fr);
    }
    
    public Color4_b Evaluate(        
        Vector3_b         worldDirGen,
        Value1Df        directPdfW,
        Value1Df        cosThetaGen)
    {
       Color4_b result = new Color4_b();

       Vector3_b localDirGen = frame.toLocal(worldDirGen); 

       if(localDirGen.z * localDirFix.z < 0)
           return result;

       cosThetaGen.x = localDirGen.z;
       
       result.addAssign(EvaluateDiffuse(localDirGen, directPdfW));
       result.addAssign(EvaluateGlossy(localDirGen, directPdfW));

       return result;
    }
    
    public Color4_b SampleBsdf(        
        Value3Df       sample,
        Vector3_b      worldDirGen,
        Value1Df       pdfW,
        Value1Df       cosThetaGen)
    {
        //select which bsdf to sample
        Events sampledEvent;
        if(sample.z < probabilities.diffProb)
           sampledEvent = kDiffuse;
        else
           sampledEvent = kGlossy;


        Color4_b result = new Color4_b();
        Vector3_b localDirGen = new Vector3_b();

        //sample the selected bsdf and evaluate the rest of the bsdfs
        switch(sampledEvent)
        {
           case kDiffuse:
           {
              result.addAssign(SampleDiffuse(sample.getXY(), localDirGen, pdfW));

              if(result.isBlack())
                 return new Color4_b();

              result.addAssign(EvaluateGlossy(localDirGen, pdfW));
              break;
           }
           case kGlossy:
           {
              result.addAssign(SampleGlossy(sample.getXY(), localDirGen, pdfW));
              if(result.isBlack())
                 return new Color4_b();
              result.addAssign(EvaluateDiffuse(localDirGen, pdfW));

              break;
           }
        }

        //calculate costheta from generated direction
        cosThetaGen.x   = abs(localDirGen.z);
        if(cosThetaGen.x < EPS_COSINE)
          return new Color4_b();

        //transform the generated local direction to world coordinate
        worldDirGen.setValue(frame.toWorld(localDirGen));

        return result;
    }
    
    float alpha(Vector3_b w, float ax, float ay)
    {
        return (float) sqrt(Frame.cos2Phi(w) * ax * ax + Frame.sin2Phi(w) * ay * ay);
    }
    
    //trowbridge-reitz distribution lambda
    float lambda(Vector3_b w, float ax, float ay)
    {
        float absTanTheta = abs(Frame.tanTheta(w));
        float a           = alpha(w, ax, ay);
        float a2tan2theta = (a * absTanTheta) * (a * absTanTheta);
        float lam         = (float) ((-1.f + sqrt(1.f + a2tan2theta))/2);
        return Float.isInfinite(absTanTheta) ? 0 : lam ;
    }
    
    //trowbridge-reitz microfacet distribution
    float D_H(Vector3_b wh, float ax, float ay)
    {
        float tan2Theta = Frame.tan2Theta(wh);
        float cos4Theta = Frame.cos2Theta(wh) * Frame.cos2Theta(wh);
        float e         = (Frame.cos2Phi(wh) / (ax * ax) +
                           Frame.sin2Phi(wh) / (ay * ay)) * tan2Theta;
        float d         =  (float) (1.f / (PI * ax * ay * cos4Theta * (1.f + e) * (1.f + e)));
        return Float.isInfinite(tan2Theta) ? 0.f : d; 
    }

    //geometric attenuation
    float G_Atten(Vector3_b w1, Vector3_b w2, float ax, float ay)
    {
        return 1.f/(1.f + lambda(w1, ax, ay) + lambda(w2, ax, ay));
    }

    float G1(Vector3_b w, float ax, float ay)
    {
        return 1.f/(1.f + lambda(w, ax, ay));
    }
    
    // Input Ve: view direction
    // Input alpha_x, alpha_y: roughness parameters
    // Input U1, U2: uniform random numbers
    // Output Ne: normal sampled with PDF D_Ve(Ne) = G1(Ve) * max(0, dot(Ve, Ne)) * D(Ne) / Ve.z
    Vector3_b sampleGGXVNDF(Vector3_b v, float ax, float ay, float r1, float r2)
    {
       // Section 3.2: transforming the view direction to the hemisphere configuration
       Vector3_b Vh    = new Vector3_b(ax * v.x, ay * v.y, v.z).normalize();

       // Section 4.1: orthonormal basis (with special case if cross product is zero)
       float lensq     = Vh.x * Vh.x + Vh.y * Vh.y;
       //Vector3_b T1       = select((float4)(1, 0, 0, 0), (float4)(-Vh.y, Vh.x, 0, 0) * rsqrt(lensq), (int4)((lensq > 0) <<31)); 
       Vector3_b T1    = lensq > 0 ? new Vector3_b(-Vh.y, Vh.x, 0).div((float)Math.sqrt(lensq)) : new Vector3_b(1, 0, 0);
       Vector3_b T2    = Vector3_b.cross(Vh, T1);

       // Section 4.2: parameterization of the projected area
       float r         = (float) sqrt(r1);
       float phi       = (float) (2.0 * PI * r2);
       float t1        = (float) (r * cos(phi));
       float t2        = (float) (r * sin(phi));
       float s         = (float) (0.5 * (1.0 + Vh.z));
       t2              = (float) ((1.0 - s)*sqrt(1.0 - t1*t1) + s*t2);

       // Section 4.3: reprojection onto hemisphere
       //Vector3_b Nh    = t1*T1 + t2*T2 + sqrt(max(0.f, 1.f - t1*t1 - t2*t2))*Vh;
       Vector3_b Nh    = T1.mul(t1).add(T2.mul(t2)).add(Vh.mul((float)sqrt(max(0.f, 1.f - t1*t1 - t2*t2))));

       // Section 3.4: transforming the normal back to the ellipsoid configuration
       Vector3_b Ne       = new Vector3_b(ax * Nh.x, ay * Nh.y, max(0.f, Nh.z)).normalize();
       return Ne;
    }
    
    //distribution of visible normals(VNDF) or Dv(N)
    float VNDF(Vector3_b v, Vector3_b n, float ax, float ay)
    {
       return G1(v, ax, ay)* max(0.001f, v.dot(n)) * D_H(n, ax, ay)/max(0.001f, v.z);
    }

    //ni is from sampled ggxvndf above
    float pdfGGXVNDF(Vector3_b v, Vector3_b ni, float ax, float ay)
    {
        float DvNi = VNDF(v, ni, ax, ay);
        float VNi  = v.dot(ni);
        return DvNi/(4 * VNi);
        //return VNi == 0.0f ? 0.0f : DvNi/(4 * VNi);
    }
    
    ///////////////////////////
    // From above Torrance_Sparrow Brdf = D(h)*G(wo, wi)*Fr(wo)/(4*cos(wo)*cos(wi))
    // based on sampling from sampleGGXVNDF
    // and pdf from pdfGGXVNDF
    ///////////////////////////


    //reflect as usual for a mirror
    //but when sampling using GGX, localize the incoming vector with sampled Ni and reflect
    //     i.e. refl(w, ni) for GGX

    Vector3_b reflectFromN(Vector3_b v, Vector3_b n)
    {
        //return -v + 2 * dot(v, n) * n; (v is incidence but pointing away from normal surface)
        return v.neg().add(n.mul(2*v.dot(n)));        
    }
    
    //v is incidence but points outwards
    //https://graphics.stanford.edu/courses/cs148-10-summer/docs/2006--degreve--reflection_refraction.pdf
    Vector3_b refractFromN(Vector3_b v, Vector3_b n, float n1, float n2)
    {
        float nratio = n1/n2;
        float cosI  = v.dot(n); //note v points outwards
        float sinT2 = nratio * nratio * (1.f - cosI * cosI);
        if(sinT2 > 1.0) return null; //TIR (use fresnel equation appropriately to avoid return of null vector)
        float cosT = (float)Math.sqrt(1.f - sinT2);
        return v.neg().mul(nratio).add(n.mul(nratio*cosI - cosT));
    }

    Vector3_b reflectLocal(Vector3_b v)
    {
        //return (float4)(-v.x, -v.y, v.z, 0);
        return reflectFromN(v, new Vector3_b(0, 0, 1));
    }

    Vector3_b halfVector(Vector3_b v1, Vector3_b v2)
    {
        Vector3_b wh = v1.add(v2);
        wh = wh.normalize();
        return wh;
    }
    
    float schlickRefl(Vector3_b v, Vector3_b n, float n1, float n2)
    {
        float r0 = (n1 - n2)/ (n1 + n2);
        r0 *= r0;
        float cosI = v.dot(n);
        if(n1 > n2)
        {
            float nratio = n1/n2;
            float sinT2 = nratio * nratio * (1.f - cosI * cosI);
            if(sinT2 > 1.f)
                return 1;
            cosI = (float)Math.sqrt(1.f - sinT2);
        }
        float x = 1.f - cosI;
        return r0 + (1.f - r0) * x * x * x * x * x;
    }
    
    //https://schuttejoe.github.io/post/ggximportancesamplingpart1/
    //f0 is float r,g,b
    Color4_b SchlickColor(float cosTheta, Color4_b f0)
    {
        
        float exponential = (float) pow(1.f - cosTheta, 5.f);
        Color4_b c = new Color4_b();
        float r = f0.r() + (1.f - f0.r()) * exponential;
        float g = f0.g() + (1.f - f0.g()) * exponential;
        float b = f0.b() + (1.f - f0.b()) * exponential;
        return c.setTo(r, g, b);
    }
}
