/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.geom;

import static coordinate.utility.Utility.INV_PI_F;
import static coordinate.utility.Utility.PI_F;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;

/**
 *
 * @author user
 */
public class Util_b {
    //////////////////////////////////////////////////////////////////////////
    /// Sample direction in the upper hemisphere with cosine-proportional pdf
    /** The returned PDF is with respect to solid angle measure
     * @param aSamples
     * @param oPdfW
     * @return  */
    public static Vector3_b SampleCosHemisphereW(
        Value2Df        aSamples,
        Value1Df        oPdfW)
    {
        float term1 = 2.f * PI_F * aSamples.x;
        float term2 = (float) Math.sqrt(1.f - aSamples.y);

        Vector3_b ret = new Vector3_b(
            (float)Math.cos(term1) * term2,
            (float)Math.sin(term1) * term2,
            (float)Math.sqrt(aSamples.y));

        if(oPdfW != null)
        {
            oPdfW.x = ret.z * INV_PI_F;
        }

        return ret;
    }
}
