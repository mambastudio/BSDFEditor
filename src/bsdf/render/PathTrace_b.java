/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.render;

import bitmap.display.ImageDisplay;
import bsdf.Scene_b;
import bsdf.abstracts.AbstractLight_b;
import bsdf.abstracts.Renderer_b;
import bsdf.geom.Color4_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Ray_b;
import bsdf.geom.Vector3_b;
import bsdf.surface.Bsdf_b;
import bsdf.ui.RenderImageFX_b;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;
import coordinate.utility.Value3Df;
import java.util.concurrent.ThreadLocalRandom;
import thread.model.LambdaThread;

/**
 *
 * @author user
 */
public class PathTrace_b implements Renderer_b<ImageDisplay> {
    Scene_b scene = null;
    RenderImageFX_b image = null;
    ImageDisplay display;
    
    LambdaThread thread = new LambdaThread();
    
    int minPathLength = 0;
    int maxPathLength = 3;
    
    public PathTrace_b()
    {
       
    }
    
    public RenderImageFX_b getImage()
    {
        return image;
    }
    
    public void render()
    {
        // We sample lights uniformly
        int   lightCount    = scene.getLightCount();
        float lightPickProb = 1.f / lightCount;

        int resX = image.getWidth();
        int resY = image.getHeight();
        
        for(int pixID = 0; pixID < resX * resY; pixID++)
        {
            thread.chill();
            
            int x = pixID % resX;
            int y = pixID / resX;
         
            Ray_b   ray   = scene.camera.generateRay(x, y, resX, resY, new Ray_b());
            Isect_b isect = new Isect_b();
           
            Color4_b pathWeight     = new Color4_b(1, 1, 1);
            Color4_b color          = new Color4_b();            
            int  pathLength         = 1;
            boolean  lastSpecular   = true;
            float lastPdfW          = 1;
            Point3_b lastHit          = new Point3_b();
            Vector3_b lastNormal      = new Vector3_b();

            for(;; ++pathLength)
            {                        
                thread.chill();
                if(!scene.intersect(ray, isect)) //No hit
                {            
                    //section for environment light
                    break;            
                }              
                
                //last hit for light grid purpose
                lastHit = isect.p.copy();
                lastNormal = isect.n.copy();
                
                
                //bsdf setup
                Bsdf_b bsdf = Bsdf_b.setupBsdf(ray, isect);
                
                if(!bsdf.isValid())
                    break;
                
              
                // directly hit some light, lights do not reflect
                //if(isect.lightID >= 0)       
                
                if(pathLength >= maxPathLength)
                    break;
                
                
                //if(bsdf.continuationProb() == 0)
                //    break;
                              
                
                // next event estimation
                if(!bsdf.isDelta() && pathLength + 1 >= minPathLength)
                {
                    int lightID = (int)(ThreadLocalRandom.current().nextFloat() * lightCount);
                    AbstractLight_b light = scene.getLightPtr(lightID);
                    
                    
                    Vector3_b directionToLight = new Vector3_b();
                    Value1Df distance = new Value1Df(), directPdfW = new Value1Df();
                    Color4_b radiance = light.illuminate(scene.getSceneSphere(), isect.p, lastNormal,
                        Value2Df.getRandom(), directionToLight, distance, directPdfW, null, null);
                                     
                    if(!radiance.isBlack())
                    {
                        Value1Df bsdfPdfW = new Value1Df(), cosThetaOut = new Value1Df();
                        Color4_b factor = bsdf.Evaluate(directionToLight, bsdfPdfW, cosThetaOut);
                        
                        if(!factor.isBlack())
                        {
                            float weight = 1.f;
                            if(!light.isDelta())
                            {
                                float contProb = 1;//bsdf.continuationProb();
                                bsdfPdfW.x *= contProb;
                                weight = Mis2(directPdfW.x * lightPickProb, bsdfPdfW.x);
                            }
                            
                            Color4_b contrib = radiance.mul(weight * cosThetaOut.x / (lightPickProb * directPdfW.x))
                                    .mul(factor);
                            
                            Ray_b rayP = new Ray_b();
                            rayP.set(isect.p, directionToLight, distance.x);
                            if(!scene.intersectP(rayP)) 
                            {
                                color.addAssign(contrib.mul(pathWeight));   
                                scene.getEnvLight().accumLightGrid(radiance, isect.n, isect.p, directionToLight);
                                
                                InfiniteAreaLight background = scene.getEnvLight();
                                if(background.doSampleFromLightGrid)
                                {
                                   // float pdf = background.pdfLightGrid(directionToLight, isect.p);
                                  //  System.out.println(pdf);
                                }
                            }

                        }
                    }
                }
                
                // continue random walk
                {
                    Value3Df rndTriplet = Value3Df.getRng();
                    Value1Df pdf = new Value1Df(), cosThetaOut = new Value1Df();
                    
                    Vector3_b d = new Vector3_b();
                    Point3_b o = ray.getPoint();
                    
                    Color4_b factor = bsdf.SampleBsdf(rndTriplet, d, pdf, cosThetaOut);
                    
                    if(factor.isBlack())
                        break;
                    
                    // Russian roulette
                    float contProb = 1;//bsdf.ContinuationProb();
                    
                    lastSpecular = bsdf.isDelta();
                    lastPdfW     = pdf.x * contProb;
                    
                    if(contProb < 1.f)
                    {
                        if(ThreadLocalRandom.current().nextFloat() > contProb)
                        {
                            break;
                        }
                        pdf.x *= contProb;
                    }
                    
                    pathWeight.mulAssign(factor.mul(cosThetaOut.x / pdf.x));
                    
                    //set new ray direction
                    ray.set(o, d);
                }
            }
            if(!color.isBad())
                image.addAccum(color.getBitmapColor(), pixID);
            
            thread.chill();
        }
        image.incrementCount();
        updateDisplay();
        if(image.count() > 0)
        {
            scene.getEnvLight().setSampleFromLightGrid(true);   
            //maxPathLength = 3;
        }
        
        scene.getEnvLight().updateLightGrid();
        System.out.println(image.count());
        thread.chill();
    }

    @Override
    public boolean prepare(Scene_b scene, int w, int h) {
        this.scene = scene;
        this.image = new RenderImageFX_b(w, h);
        return true;
    }

    @Override
    public void startExecution(ImageDisplay display) {        
        this.scene.getCamera().setUp(); //initiate transform matrix
        this.display = display;      
        clearImage(); 
        thread.startExecution(this::render);
       
    }

    @Override
    public void stop() {
        thread.stopExecution();
    }

    @Override
    public void pause() {
        thread.pauseExecution();
    }

    @Override
    public void resume() {
        thread.resumeExecution();
    }

    @Override
    public void updateDisplay() {
        image.update();
       
        display.imageFill(image);
        
        
        //System.out.println(image.readColor(0, 0));
        
    }

    @Override
    public void trigger() {
        thread.resumeExecution();
    }

    @Override
    public boolean isRunning() {
        return thread.isTerminated();
    }
    
    // Mis power (1 for balance heuristic)
    private float Mis(float aPdf)
    {
        return aPdf;
    }

    // Mis weight for 2 pdfs
    private float Mis2(
        float aSamplePdf,
        float aOtherPdf)
    {
        return Mis(aSamplePdf) / (Mis(aSamplePdf) + Mis(aOtherPdf));
    }
    
    public void clearImage()
    {
        this.image.clear();
        display.imageFill(image);
    }
}
