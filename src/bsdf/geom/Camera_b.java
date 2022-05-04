/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import coordinate.model.CameraModel;
import coordinate.model.Transform;

/**
 *
 * @author user
 */
public class Camera_b extends CameraModel <Point3_b, Vector3_b, Ray_b>{
    
    public Camera_b(Point3_b position, Point3_b lookat, Vector3_b up, float horizontalFOV) {
        super(position.copy(), lookat.copy(), up.copy(), horizontalFOV);
    }
    
    public void set(Point3_b position, Point3_b lookat, Vector3_b up, float horizontalFOV)
    {
        this.position = position.copy();
        this.lookat = lookat.copy();
        this.up = up.copy();
        this.fov = horizontalFOV;
        this.cameraTransform = new Transform<>();
    }

    @Override
    public Camera_b copy() {
        Camera_b camera = new Camera_b(position, lookat, up, fov);
        camera.setUp();
        return camera;
    }
}
