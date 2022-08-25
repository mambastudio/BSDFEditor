/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf;

import bsdf.abstracts.AbstractLight_b;
import bsdf.abstracts.EnvLight_b;
import bsdf.abstracts.Primitive_b;
import bsdf.accelerator.BVH_b;
import bsdf.geom.BBox_b;
import bsdf.geom.Camera_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Ray_b;
import bsdf.geom.SceneSphere_b;
import bsdf.geom.Vector3_b;
import bsdf.light.LightCache_b;
import bsdf.light.NullBackground;
import bsdf.primitive.TriangleMesh_b;
import bsdf.surface.Material_b;
import coordinate.model.OrientationModel;
import coordinate.parser.obj.OBJInfo;
import coordinate.parser.obj.OBJParser;

/**
 *
 * @author user
 */
public class Scene_b {
    public Camera_b camera; 
    public TriangleMesh_b triangleMesh;    
    public OrientationModel<Point3_b, Vector3_b, Ray_b, BBox_b> orientation = new OrientationModel(Point3_b.class, Vector3_b.class);
    public BVH_b bvh;
    
    private final LightCache_b lightCache = new LightCache_b();
    
    public Scene_b() {
        this.camera = new Camera_b(
                new Point3_b(0.27739, 2.21805, 6.60308), 
                new Point3_b(0.26485, 2.19267, -0.24414), 
                new Vector3_b(-0.00473, 0.99781, 0.06587), 
                45);
        
        OBJParser parser = new OBJParser();
        parser.setSplitPolicy(OBJInfo.SplitOBJPolicy.USEMTL);
        
        bvh = new BVH_b();
        
        triangleMesh = new TriangleMesh_b();
        parser.readFile(Scene_b.class, "Material Ball.obj", triangleMesh);

        bvh.build(triangleMesh);
        
        //enable light
        Material_b light = triangleMesh.getMaterial(1);
        light.enableEmitter();
        
        lightCache.addMeshLights(triangleMesh);    
        
    } 

    public Camera_b getCamera() {
        return camera;
    }
    
    public boolean intersect(Ray_b ray, Isect_b isect)
    {
        
        return bvh.intersect(ray, isect);
    }
    
    public boolean intersectP(Ray_b ray)
    {
        return bvh.intersectP(ray);
    }
        
    public void setCamera(Camera_b camera)
    {
        this.camera = camera;
    }
    
    public Primitive_b getPrimitive()
    {
        return triangleMesh;
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
        return triangleMesh.getBound();
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
    
    public AbstractLight_b getLightFromPrimID(int index)
    {
        return lightCache.getLightFromPrimID(index);
    }   
    
    public AbstractLight_b getRandomLight()
    {
        return lightCache.getRandomLight();
    }
    
    public Material_b getMaterial(int index)
    {
        return triangleMesh.getMaterial(index);
    }
}
