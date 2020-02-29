import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class ApplicationStart extends Application {
    //Root
    private Pane root = new Pane();

    //Root constructor, returns root as parent type
    private Parent createContent(){
        root.setPrefSize(1920,1080);
        return root;
    }

    public void start(Stage stage){
        stage.setTitle("GlowLine");
        stage.setFullScreen(false);

        Scene scene = new Scene(createContent());
        stage.setScene(scene);

        stage.show();
    }

    /********************Launch***********************/
    public static void main(String[] args){
        launch(args);
    }

}
