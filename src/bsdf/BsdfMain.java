package bsdf;

import bsdf.ui.MaterialFX_b;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class BsdfMain extends Application{
    
    public static void main(String... args)
    {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        StackPane root = new StackPane();
        Scene scene = new Scene(root, 600, 500);
       
        root.getChildren().add(new MaterialEditor(new MaterialFX_b()));
        
        primaryStage.setTitle("Hello World!");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
}
