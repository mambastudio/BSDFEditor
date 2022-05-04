/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import bsdf.surface.Material_b;
import coordinate.generic.raytrace.AbstractIntersection;
import bsdf.abstracts.Primitive_b;

/**
 *
 * @author user
 */
public class Isect_b implements AbstractIntersection{
   public Point3_b p;
   public Vector3_b n;
   public Vector3_b ng;
   public Vector3_b d;
   public Point2_b  uv;
   public Material_b mat;
   public int id;
   public Primitive_b primitive;
   public boolean hit;  
   
   public Isect_b()
   {
       this.p = new Point3_b();
       this.n = new Vector3_b();
       this.d = new Vector3_b();
       this.uv = new Point2_b();
       this.mat = null;
       this.id = -1;
       this.hit = false;
       this.primitive = null;
   }
}
