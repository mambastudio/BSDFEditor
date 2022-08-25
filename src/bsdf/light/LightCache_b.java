/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.light;

import bsdf.abstracts.AbstractLight_b;
import bsdf.primitive.TriangleMesh_b;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;



/**
 *
 * @author user
 */
public class LightCache_b {
    
    private final ArrayList<AbstractLight_b> lights = new ArrayList<>();
    private TriangleMesh_b mesh;
    
    public LightCache_b()
    {
        
    }
    
    public void addMeshLights(TriangleMesh_b mesh)
    {
        this.mesh = mesh;
        
        for(int primID = 0; primID<mesh.getSize(); primID++)
        {
            if(mesh.getMaterialFromPrimID(primID).isEmitter())
                lights.add(new AreaLight_b(mesh, primID));                
        }
    }

    public boolean hasEnvLight() {
        return false;
    }

    public boolean hasLight() {
        return lights.size()>0;
    }

    public void removeEnvLight() {
        
    }

    public int getCount() {
        return lights.size();
    }

    public AbstractLight_b getLightFromPrimID(int primID) {
        return new AreaLight_b(mesh, primID);
    }
    
    public AbstractLight_b getRandomLight()
    {
        return lights.get(getRandomElementIndex());
    }
    
    //https://www.baeldung.com/java-random-list-element
    private int getRandomElementIndex()
    {
        int randomElementIndex = ThreadLocalRandom.current().nextInt(lights.size()) % lights.size();        
        return randomElementIndex;
    }
    
    public float randomLightPdf()
    {
        if(lights.size()>0)
            return 1.f/lights.size();
        else
            return 0.f;
    }
}
