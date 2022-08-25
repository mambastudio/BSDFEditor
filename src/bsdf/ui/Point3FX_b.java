/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.ui;

import bsdf.abstracts.VoidConsumer_b;
import bsdf.geom.Point3_b;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.FloatBinding;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ObservableValue;

/**
 *
 * @author user
 * @param <T>
 */
public class Point3FX_b<T> {
    public FloatProperty x;
    public FloatProperty y;
    public FloatProperty z;
    
    private FloatBinding computed = null;
    private VoidConsumer_b consumer = null;
    
    public Point3FX_b()
    {
        init();
    }
    
    public Point3FX_b(Point3_b p)
    {
        this();
        x.setValue(p.get(0));
        y.setValue(p.get(1));
        z.setValue(p.get(2));
    }
    
    public Point3FX_b(float fx, float fy, float fz)
    {
        this();
        x.setValue(fx);
        y.setValue(fy);
        z.setValue(fz);
    }
    
    private void init()
    {
        x = new SimpleFloatProperty();
        y = new SimpleFloatProperty();
        z = new SimpleFloatProperty();
        
        this.computed = Bindings.createFloatBinding(
                () -> x.get() + y.get() + z.get(),
                x, y, z);
        this.computed.addListener((o, ov, nv)->{
            if(consumer != null)
                consumer.run();
        });
    }
    
    
    public float getX()
    {
        return x.floatValue();
    }
    
    public float getY()
    {
        return y.floatValue();
    }
    
    public float getZ()
    {
        return z.floatValue();
    }
    
    public void setX(float v)
    {
        x.setValue(v);
    }
    
    public void setY(float v)
    {
        y.setValue(v);
    }
    
    public void setZ(float v)
    {
        z.setValue(v);
    }
    
    public void set(Point3FX_b point)
    {
        this.x.set(point.getX());
        this.y.set(point.getY());
        this.z.set(point.getZ());
    }
    
    public FloatProperty getXProperty()
    {
        return x;
    }
    
    public FloatProperty getYProperty()
    {
        return y;
    }
    
    public FloatProperty getZProperty()
    {
        return z;
    }
    
    public Point3_b getPoint3()
    {
        return new Point3_b(x.floatValue(), y.floatValue(), z.floatValue());
    }
    
    public void set(Point3_b p)
    {
        x.setValue(p.get(0));
        y.setValue(p.get(1));
        z.setValue(p.get(2));
    }
    
    public ObservableValue getObservable()
    {
        return computed;
    }
    
    public void listenChange(VoidConsumer_b consumer)
    {
        this.consumer = consumer;
    }
}
