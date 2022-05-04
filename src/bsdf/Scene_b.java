/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf;

import bsdf.abstracts.AbstractLight_b;
import bsdf.abstracts.EnvLight_b;
import bsdf.abstracts.Primitive_b;
import bsdf.geom.BBox_b;
import bsdf.geom.Camera_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Ray_b;
import bsdf.geom.SceneSphere_b;
import bsdf.geom.Vector3_b;
import bsdf.light.NullBackground;
import coordinate.model.OrientationModel;

/**
 *
 * @author user
 */
public class Scene_b {
    public Camera_b camera; 
    public Primitive_b primitive;
    public OrientationModel<Point3_b, Vector3_b, Ray_b, BBox_b> orientation = new OrientationModel(Point3_b.class, Vector3_b.class);
    
    private final LightCache lightCache = new LightCache();
    
    public Scene_b() {
        this.camera = new Camera_b(new Point3_b(0, 0, 4), new Point3_b(), new Vector3_b(0, 1, 0), 45);
    } 

    public Camera_b getCamera() {
        return camera;
    }
    
    public boolean intersect(Ray_b ray, Isect_b isect)
    {
        
        return primitive.intersect(ray, isect);
    }
    
    public boolean intersectP(Ray_b ray)
    {
        return primitive.intersectP(ray);
    }
        
    public void setPrimitive(Primitive_b primitive)
    {
        this.primitive = primitive;
    }
    
    public void setCamera(Camera_b camera)
    {
        this.camera = camera;
    }
    
    public Primitive_b getPrimitive()
    {
        return primitive;
    }
    
    public SceneSphere_b getSceneSphere()
    {
        SceneSphere_b sceneSphere = new SceneSphere_b();
        BBox_b worldBound = getWorldBound();
        sceneSphere.sceneCenter = worldBound.getCenter();
        sceneSphere.sceneRadius = worldBound.getMaximumExtent()/2.f;
        sceneSphere.invSceneRadiusSqr = 1.f/sceneSphere.sceneRadius;
        return sceneSphere;
    }
    
    public BBox_b getWorldBound()
    {        
        return primitive.getBound();
    }
    
    public void addLight(AbstractLight_b light)
    {
        lightCache.addLight(light);
    }
    
    public EnvLight_b getEnvLight()
    {
        return new NullBackground();
    }
    
    public boolean hasEnvLight()
    {
        return lightCache.hasEnvLight();
    }
    
    public boolean hasLight()
    {
        return lightCache.hasLight();
    }
    
    public void removeEnvLight()
    {
        lightCache.removeEnvLight();
    }
    
    public int getLightCount()
    {
        return lightCache.getCount();
    }
    
    public AbstractLight_b getLightPtr(int lightID)
    {
        return lightCache.getLightPtr(lightID);
    }   
}
