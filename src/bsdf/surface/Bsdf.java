/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Color4;
import bsdf.geom.Isect;
import bsdf.geom.Ray;
import static bsdf.geom.Util.SampleCosHemisphereW;
import bsdf.geom.Vector3;
import static coordinate.utility.Utility.EPS_COSINE;
import static coordinate.utility.Utility.INV_PI_F;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;
import coordinate.utility.Value3Df;
import static java.lang.Float.max;
import static java.lang.Math.abs;
import static bsdf.surface.Bsdf.Events.kDiffuse;
import static bsdf.surface.Bsdf.Events.kGlossy;

/**
 *
 * @author user
 */
public class Bsdf {
    
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
    
    public SurfaceParameter  param = null;                 //chosen surface
    public Frame frame = null;                    //local frame of reference

    public Vector3 localDirFix = null;                      //incoming (fixed) incoming direction, in local
    public Vector3 localGeomN = null;                       //geometry normal (without normal shading) 
    
    public ComponentProbabilities probabilities = null;    //sampling probabilities
    
    public Material material = null;
    
    private Bsdf()
    {
        
    }
    
    
    public static Bsdf setupBsdf(Ray ray, Isect isect)
    {
        Bsdf bsdf = new Bsdf();
        
        if(isect.hit)
        {            
            //frame for local surface
            bsdf.frame = new Frame(isect.n);
                       
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
            bsdf.probabilities = new ComponentProbabilities();            
            getComponentProbabilities(bsdf, bsdf.probabilities);            
        }
        return bsdf;
    }
    
    public boolean isValid()
    {
        return material != null;
    }
    
    public boolean isDelta()
    {
        return false;
    }
    
    ////////////////////////////////////////////////////////////////////////////
    // Albedo methods
    ////////////////////////////////////////////////////////////////////////////

    static float AlbedoDiffuse(Bsdf bsdf)
    {
       Color4 color = bsdf.param.diffuse_color.mul(bsdf.param.diffuse_param.x);
       return color.luminance();
    }

    static float AlbedoGlossy(Bsdf bsdf)
    {
       Color4 color = bsdf.param.glossy_color.mul(bsdf.param.glossy_param.x);
       return color.luminance();
    }

    static float AlbedoReflect(Bsdf bsdf)
    {
       Color4 color = bsdf.param.mirror_color.mul(bsdf.param.mirror_param.x);
       return color.luminance();
    }

    static float AlbedoRefract(Bsdf bsdf)
    {
       return 0;
    }
    
    static  void getComponentProbabilities(Bsdf bsdf, ComponentProbabilities probabilities)
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
    
    public Color4 SampleDiffuse(       
        Value2Df       sample,
        Vector3        localDirGen,
        Value1Df       pdfW)
    {
        if(localDirFix.z < EPS_COSINE)
          return new Color4();

        Value1Df unweightedPdfW = new Value1Df();
        localDirGen.setValue(SampleCosHemisphereW(sample, unweightedPdfW));
        pdfW.x += unweightedPdfW.x * probabilities.diffProb;

        return param.diffuse_color.mul(INV_PI_F); 
    }
    
    public Color4 SampleGlossy(        
        Value2Df       sample,
        Vector3        localDirGen,
        Value1Df       pdfW)
    {
        return null;
    }
    
    public Color4 EvaluateDiffuse(        
        Vector3        localDirGen,
        Value1Df       directPdfW)
    {
        if(probabilities.diffProb == 0)
          return new Color4();

        if(localDirFix.z < EPS_COSINE || localDirGen.z < EPS_COSINE)
          return new Color4();

        if(directPdfW != null)
            directPdfW.x += probabilities.diffProb * max(0.f, localDirGen.z * INV_PI_F);

       return param.diffuse_color.mul(INV_PI_F); 
    }
    
    public Color4 EvaluateGlossy(        
        Vector3        localDirGen,
        Value1Df       directPdfW)
    {
        return new Color4();
    }
    
    public Color4 Evaluate(        
        Vector3         worldDirGen,
        Value1Df        directPdfW,
        Value1Df        cosThetaGen)
    {
       Color4 result = new Color4();

       Vector3 localDirGen = frame.toLocal(worldDirGen); 

       if(localDirGen.z * localDirFix.z < 0)
           return result;

       cosThetaGen.x = localDirGen.z;
       
       result.addAssign(EvaluateDiffuse(localDirGen, directPdfW));
       result.addAssign(EvaluateGlossy(localDirGen, directPdfW));

       return result;
    }
    
    public Color4 SampleBsdf(        
        Value3Df       sample,
        Vector3        worldDirGen,
        Value1Df       pdfW,
        Value1Df       cosThetaGen)
    {
        //select which bsdf to sample
        Events sampledEvent;
        if(sample.z < probabilities.diffProb)
           sampledEvent = kDiffuse;
        else
           sampledEvent = kGlossy;


        Color4 result = new Color4();
        Vector3 localDirGen = new Vector3();

        //sample the selected bsdf and evaluate the rest of the bsdfs
        switch(sampledEvent)
        {
           case kDiffuse:
           {
              result.addAssign(SampleDiffuse(sample.getXY(), localDirGen, pdfW));

              if(result.isBlack())
                 return new Color4();

              result.addAssign(EvaluateGlossy(localDirGen, pdfW));
              break;
           }
           case kGlossy:
           {
              result.addAssign(SampleGlossy(sample.getXY(), localDirGen, pdfW));
              if(result.isBlack())
                 return new Color4();
              result.addAssign(EvaluateDiffuse(localDirGen, pdfW));

              break;
           }
        }

        //calculate costheta from generated direction
        cosThetaGen.x   = abs(localDirGen.z);
        if(cosThetaGen.x < EPS_COSINE)
          return new Color4();

        //transform the generated local direction to world coordinate
        worldDirGen.setValue(frame.toWorld(localDirGen));

        return result;
    }
}
