/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.surface;

import bsdf.geom.Color4_b;

/**
 *
 * @author user
 */
public class MaterialUtility_b {
    //https://github.com/mitsuba-renderer/mitsuba/blob/cfeb7766e7a1513492451f35dc65b86409655a7b/src/libcore/util.cpp
    public static Color4_b fresnelConductorExact(float cosThetaI, Color4_b eta, Color4_b k) 
    {
        float cosThetaI2    = cosThetaI*cosThetaI,
              sinThetaI2    = 1-cosThetaI2,
              sinThetaI4    = sinThetaI2*sinThetaI2;
        
        Color4_b temp1      = eta.mul(eta).sub(k.mul(k)).sub(new Color4_b(sinThetaI2)),
                 a2pb2      = (temp1.mul(temp1).add(k.mul(k).mul(eta).mul(eta).mul(4))).sqrt(),
                 a          = ((a2pb2.add(temp1)).mul(0.5f)).sqrt();
        
        Color4_b term1      = a2pb2.add(new Color4_b(cosThetaI2)),
                 term2      = a.mul(2 * cosThetaI);
        
        Color4_b Rs2        = term1.sub(term2).div(term1.add(term2));
        
        Color4_b term3      = a2pb2.mul(cosThetaI2).add(new Color4_b(sinThetaI4)),
                 term4      = term2.mul(sinThetaI2);
        
        Color4_b Rp2        = Rs2.mul(term3.sub(term4)).div(term3.add(term4));
        
        return Rp2.add(Rs2).mul(0.5f);
    }
}
