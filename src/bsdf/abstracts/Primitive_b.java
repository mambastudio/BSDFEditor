/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.abstracts;

import bsdf.geom.BBox_b;
import bsdf.geom.Isect_b;
import bsdf.geom.Point3_b;
import bsdf.geom.Ray_b;
import bsdf.surface.Material_b;
import coordinate.generic.raytrace.AbstractAccelerator;
import coordinate.generic.raytrace.AbstractPrimitive;

/**
 *
 * @author user
 */
public interface Primitive_b extends AbstractPrimitive<Point3_b, Ray_b, Isect_b, AbstractAccelerator<Ray_b, Isect_b, Primitive_b, BBox_b>, BBox_b>{
    public Material_b getMaterial(int index);
}
