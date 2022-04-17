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
public class Camera extends CameraModel <Point3, Vector3, Ray>{
    
    public Camera(Point3 position, Point3 lookat, Vector3 up, float horizontalFOV) {
        super(position.copy(), lookat.copy(), up.copy(), horizontalFOV);
    }
    
    public void set(Point3 position, Point3 lookat, Vector3 up, float horizontalFOV)
    {
        this.position = position.copy();
        this.lookat = lookat.copy();
        this.up = up.copy();
        this.fov = horizontalFOV;
        this.cameraTransform = new Transform<>();
    }

    @Override
    public Camera copy() {
        Camera camera = new Camera(position, lookat, up, fov);
        camera.setUp();
        return camera;
    }
}
