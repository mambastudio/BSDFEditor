/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.render;

import bitmap.display.ImageViewDisplay;
import bsdf.Scene_b;
import bsdf.abstracts.AbstractLight_b;
import bsdf.abstracts.Renderer_b;
import bsdf.geom.Color4_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Ray_b;
import bsdf.geom.Vector3_b;
import bsdf.surface.Bsdf_b;
import bsdf.ui.RenderImageFX_b;
import coordinate.utility.Utility;
import static coordinate.utility.Utility.mis2;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;
import coordinate.utility.Value3Df;
import thread.model.LoopOnce;
import thread.model.LoopPool;

/**
 *
 * @author user
 */
public class PathTrace_b implements Renderer_b<ImageViewDisplay> {
    Scene_b scene = null;
    RenderImageFX_b image = null;
    ImageViewDisplay display;
    
    LoopPool threads = new LoopPool();
    
    int minPathLength = 0;
    int maxPathLength = 4;
    
    boolean shouldClearImage = false;
    
    public PathTrace_b()
    {
       
    }
    
    public RenderImageFX_b getImage()
    {
        return image;
    }
    
    public void render(LoopOnce thread)
    {
        // We sample lights uniformly
        int   lightCount    = scene.getLightCount();
        float lightPickProb = 1.f / lightCount;

        int resX = image.getWidth();
        int resY = image.getHeight();
        
        for(int pixID = 0; pixID < resX * resY; pixID++)
        {
            //pause or stop
            thread.chill();
            if(thread.isStopped())
                return;
            
            int x = pixID % resX;
            int y = pixID / resX;
         
            Ray_b   ray   = scene.camera.generateRayJitter(x, y, resX, resY, new Ray_b());
            Isect_b isect = new Isect_b();
            
            Color4_b pathWeight         = new Color4_b(1, 1, 1);
            Color4_b color              = new Color4_b();            
            int  pathLength             = 1;
            boolean  lastSpecular       = true;
            float lastPdfW              = 1;
                      
            //pause or stop
            thread.chill();
            if(thread.isStopped())
                return;
            
            for(;; ++pathLength)
            {
                if(!scene.intersect(ray, isect)) //No hit
                {
                    //section for environment light
                    if(pathLength < minPathLength)
                        break;
                    
                    if(!scene.hasEnvLight())
                        break;
                } 
                
                Bsdf_b bsdf = Bsdf_b.setupBsdf(ray, isect);
                if(!bsdf.isValid())
                    break;
                
                // directly hit some light, lights do not reflect
                if(isect.mat.isEmitter())
                {
                    if(pathLength < minPathLength)
                        break;

                    AbstractLight_b light = scene.getLightFromPrimID(isect.id);
                    Value1Df directPdfA = new Value1Df();
                    Color4_b contrib = light.getRadiance(scene.getSceneSphere(), ray.d, isect.p, directPdfA, null);                            
                    if(contrib.isBlack())
                        break;

                    float misWeight = 1.f;
                    if(pathLength > 1 && !lastSpecular)
                    {
                        float directPdfW = Utility.pdfAtoW(directPdfA.x, ray.getMax(),
                            bsdf.cosThetaFix());
                        misWeight = Mis2(lastPdfW, directPdfW * lightPickProb);
                    }

                    color.addAssign(pathWeight.mul(misWeight).mul(contrib));
                    break;
                }
                
                //pause or stop
                thread.chill();
                if(thread.isStopped())
                    return;
                
                if(pathLength >= maxPathLength)
                    break;

                //if(bsdf.ContinuationProb() == 0)
                //    break;
                
                // next event estimation
                if(!bsdf.isDelta() && pathLength + 1 >= minPathLength)
                {                    
                    AbstractLight_b light = scene.getRandomLight();

                    Vector3_b directionToLight = new Vector3_b();
                    Value1Df distance = new Value1Df(), directPdfW = new Value1Df();
                    Color4_b radiance = light.illuminate(
                            scene.getSceneSphere(), isect.p, directionToLight, 
                            Value2Df.getRandom(), directionToLight, distance, 
                            directPdfW, directPdfW, null);
                           
                    if(!radiance.isBlack())
                    {
                        Value1Df bsdfPdfW = new Value1Df(), cosThetaOut = new Value1Df();
                        Color4_b factor = bsdf.Evaluate(directionToLight, bsdfPdfW, cosThetaOut);

                        if(!factor.isBlack())
                        {
                            float weight = 1.f;
                            if(!light.isDelta())
                            {
                                //float contProb = bsdf.ContinuationProb();
                                //bsdfPdfW *= contProb;
                                weight = mis2(directPdfW.x * lightPickProb, bsdfPdfW.x);
                            }

                            Color4_b contrib = (Color4_b) radiance.mul(factor).mul(weight * cosThetaOut.x / (lightPickProb * directPdfW.x));

                            Ray_b occludeRay = new Ray_b();
                            occludeRay.set(isect.p, directionToLight, distance.x);
                            if(!scene.intersectP(occludeRay))
                            {
                                color.addAssign(contrib.mul(pathWeight));
                            }
                        }
                    }
                }
                
                // continue random walk
                {
                    Value3Df rndTriplet = Value3Df.getRng();
                    Value1Df pdf = new Value1Df(), cosThetaOut = new Value1Df();
                    
                    Vector3_b newDir = new Vector3_b();
                    Color4_b factor = bsdf.SampleBsdf(rndTriplet, newDir, pdf, cosThetaOut);
                            
                    if(factor.isBlack())
                        break;

                    // Russian roulette
                    //float contProb = bsdf.ContinuationProb();

                    lastSpecular = bsdf.isDelta();
                    
                    lastPdfW     = pdf.x;// * contProb;


                    pathWeight.mulAssign(factor.mul(cosThetaOut.x / pdf.x));
                    // We offset ray origin instead of setting tmin due to numeric
                    // issues in ray-sphere intersection. The isect.dist has to be
                    // extended by this EPS_RAY after hitpoint is determined
                    ray = new Ray_b();
                    ray.set(isect.p, newDir);
                }
            }
            image.addAccum(color.getBitmapColor(), pixID);
        }        
        image.incrementCount();        
        //updateDisplay(); but this updated after thread is executed.
    }
    
    protected void reset()
    {
        clearImage(); 
    }
    

    @Override
    public boolean prepare(Scene_b scene, int w, int h) {
        this.scene = scene;
        this.image = new RenderImageFX_b(w, h);
        this.image.setExposure(0.3f);
        return true;
    }

    @Override
    public void startExecution(ImageViewDisplay display) {        
        this.scene.getCamera().setUp(); //initiate transform matrix
        this.display = display;      
        clearImage(); 
        threads.execute(this::render, ()->{
            if(shouldClearImage)
            {
                clearImage();
                shouldClearImage = false;
            }
            updateDisplay();  //this is called once this::render call in thread pool is completed   
            
        });
       
    }

    @Override
    public void stop() {
        threads.stop();
    }

    @Override
    public void pause() {
        threads.pause();
    }

    @Override
    public void resume() {
        threads.resume();
    }
    

    @Override
    public void updateDisplay() {
        image.update();       
        display.imageFill(image);     
    }

    @Override
    public void trigger() {
        //thread.resumeExecution();
    }

    @Override
    public boolean isRunning() {
        //return thread.isTerminated();
        return true;
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
    
    public void shouldClearImage()
    {
        shouldClearImage = true;
    }
}
