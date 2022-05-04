/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.light;

import bitmap.Color;
import bsdf.abstracts.EnvLight_b;
import bsdf.geom.Color4_b;
import bsdf.geom.Point3_b;
import bsdf.geom.SceneSphere_b;
import bsdf.geom.Vector3_b;
import coordinate.utility.Value1Df;
import coordinate.utility.Value2Df;

/**
 *
 * @author user
 */
public class NullBackground implements EnvLight_b{

    @Override
    public Color4_b illuminate(SceneSphere_b aSceneSphere, Point3_b aReceivingPosition, Vector3_b aNormal, Value2Df aRndTuple, Vector3_b oDirectionToLight, Value1Df oDistance, Value1Df oDirectPdfW, Value1Df oEmissionPdfW, Value1Df oCosAtLight) {
        return new Color4_b();
    }

    @Override
    public Color4_b emit(SceneSphere_b aSceneSphere, Value2Df aDirRndTuple, Value2Df aPosRndTuple, Point3_b oPosition, Vector3_b oDirection, Value1Df oEmissionPdfW, Value1Df oDirectPdfA, Value1Df oCosThetaLight) {
        return new Color4_b();
    }

    @Override
    public Color4_b getRadiance(SceneSphere_b aSceneSphere, Vector3_b aRayDirection, Point3_b aHitPoint, Value1Df oDirectPdfA, Value1Df oEmissionPdfW) {
        return new Color4_b();
    }
    
}
