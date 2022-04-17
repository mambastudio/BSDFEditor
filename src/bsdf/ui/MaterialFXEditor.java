/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package bsdf.ui;

import bitmap.util.ImageUtility;
import javafx.geometry.Insets;
import javafx.scene.control.Separator;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import jfx.form.PropertyNode;
import jfx.form.Setting;
import jfx.form.SimpleSetting;

/**
 *
 * @author user
 */
public class MaterialFXEditor extends HBox{
    private final MaterialFX materialFX;
    
    private VBox leftNode = null;
    private SurfaceParameterFXEditor rightNode = null;
    
    private ImageView renderImageView;
    
    public MaterialFXEditor(MaterialFX mat)
    {
        this.materialFX = mat;        
        init();
    }
    
    public final void init()
    {
        leftNode = new VBox();
        rightNode = new SurfaceParameterFXEditor(materialFX.param);
        rightNode.setFitToWidth(true);
        HBox.setHgrow(rightNode, Priority.ALWAYS);
        
        renderImageView = new ImageView();
        renderImageView.setImage(ImageUtility.stringToImage("coming soon", 340, 230));        
        leftNode.getChildren().add(renderImageView);
        leftNode.setSpacing(5);
        
        //string name
        PropertyNode matName = SimpleSetting.createForm(
                Setting.of("Name", materialFX.name));
        leftNode.getChildren().add(new Separator());
        leftNode.getChildren().add(matName);
        leftNode.getChildren().add(new Separator());
                
        getChildren().addAll(leftNode, rightNode);
        setSpacing(5);
        this.setPadding(new Insets(3, 3, 3, 3));
        
        this.requestFocus();
    }
    
    public MaterialFX getEditedMaterial()
    {
        
        return materialFX;
    }
}
