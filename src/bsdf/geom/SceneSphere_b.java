/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

/**
 *
 * @author user
 */
public class SceneSphere_b {
    // Center of the scene's bounding sphere
    public Point3_b sceneCenter;
    // Radius of the scene's bounding sphere
    public float sceneRadius;
    // 1.f / (mSceneRadius^2)
    public float invSceneRadiusSqr;
    
    public SceneSphere_b()
    {
        sceneCenter = new Point3_b();
        sceneRadius = 0;
        invSceneRadiusSqr = 0;
    }
}
