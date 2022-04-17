/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.abstracts;

import bsdf.geom.BBox;
import bsdf.geom.Isect;
import bsdf.geom.Point3;
import bsdf.geom.Ray;
import bsdf.surface.Material;
import coordinate.generic.raytrace.AbstractAccelerator;
import coordinate.generic.raytrace.AbstractPrimitive;

/**
 *
 * @author user
 */
public interface Primitive extends AbstractPrimitive<Point3, Ray, Isect, AbstractAccelerator<Ray, Isect, Primitive, BBox>, BBox>{
    public Material getMaterial(int index);
}
