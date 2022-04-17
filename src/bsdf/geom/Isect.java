/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import bsdf.abstracts.Primitive;
import bsdf.surface.Material;
import coordinate.generic.raytrace.AbstractIntersection;

/**
 *
 * @author user
 */
public class Isect implements AbstractIntersection{
   public Point3 p;
   public Vector3 n;
   public Vector3 ng;
   public Vector3 d;
   public Point2  uv;
   public Material mat;
   public int id;
   public Primitive primitive;
   public boolean hit;  
   
   public Isect()
   {
       this.p = new Point3();
       this.n = new Vector3();
       this.d = new Vector3();
       this.uv = new Point2();
       this.mat = null;
       this.id = -1;
       this.hit = false;
       this.primitive = null;
   }
}
