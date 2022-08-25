/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf;

import bitmap.display.ImageViewDisplay;
import bsdf.render.PathTrace_b;
import bsdf.surface.Material_b;
import bsdf.ui.MaterialFX_b;
import bsdf.ui.SurfaceParameterFXEditor_b;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfx.form.PropertyNode;
import jfx.form.Setting;
import jfx.form.SimpleSetting;
import jfx.util.ImplUtils;

/**
 *
 * @author user
 * @param <T>
 */
public class MaterialEditor<T> extends HBox{
    private final MaterialFX_b materialFX;
    
    private VBox leftNode = null;
    private SurfaceParameterFXEditor_b rightNode = null;
    
    private ImageViewDisplay renderImageView;
    
    private static Scene_b scene;
    private static PathTrace_b pathtrace;
    
    public MaterialEditor(MaterialFX_b mat)
    {
        this.materialFX = mat;           
        init();
    }
    
    public final void init()
    {
        leftNode = new VBox();
        rightNode = new SurfaceParameterFXEditor_b(materialFX.param);
        rightNode.setFitToWidth(true);
        HBox.setHgrow(rightNode, Priority.ALWAYS);
        
        renderImageView = new ImageViewDisplay();
        leftNode.getChildren().add(renderImageView);
        leftNode.setSpacing(5);
        
        //string name
        PropertyNode matName = SimpleSetting.createForm(40, 10,
                Setting.of("Name", materialFX.name));
        leftNode.getChildren().add(new Separator());
        leftNode.getChildren().add(matName);
        leftNode.getChildren().add(new Separator());
        
        leftNode.setAlignment(Pos.TOP_CENTER);
                
        getChildren().addAll(leftNode, rightNode);
        setSpacing(5);
        this.setPadding(new Insets(3, 3, 3, 3));
        
        this.requestFocus();
        
        scene = new Scene_b();        
        pathtrace = new PathTrace_b();        
        pathtrace.prepare(scene, 200, 175);
        pathtrace.startExecution(renderImageView);
        
        ImplUtils.registerScenePropertyChanged(this, (o, ov, nv)->{
           if(nv == false)
               pathtrace.stop();
        });
        
    }
    
    public static PathTrace_b getPathTracer()
    {
        return pathtrace;
    }
    
    public static Material_b getSceneMaterial(int index)
    {
        return scene.getMaterial(index);
    }    
}
