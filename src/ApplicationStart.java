import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;


public class ApplicationStart extends Application {
    //Root
    private Pane root = new Pane();

    //Player ref
    Player player = new Player();

    //input bools
    Boolean forward = false;
    Boolean backwards = false;
    Boolean left = false;
    Boolean right = false;

    //Application setup, returns root as parent type
    private Parent createContent(){
        root.setPrefSize(1920,1080);

        spawnGameObject(player,960, 900);

        /***********************************************************
         * Game Loop
         ***********************************************************/

        AnimationTimer timer = new AnimationTimer() {
            long previousTime = 0;
            double deltaT = 0;

            @Override
            public void handle(long now) {
                deltaT = (now - previousTime)*0.000_000_001;
                previousTime = now;
                update(deltaT);
            }
        };
        timer.start();

        return root;
    }

    /***********************************************************
     * Game Loop update
     ***********************************************************/
    private void update(double deltaTime){
        //System.out.println(deltaTime);

        //Accelerate player
        int accel = player.getAcceleration();
        if (forward){
            player.setVelocity(player.getVelocity().add(0,-accel));
            System.out.println("For");
        }
        if (backwards){
            player.setVelocity(player.getVelocity().add(0,accel));
            System.out.println("back");
        }
        if (left){
            player.setVelocity(player.getVelocity().add(-accel,0));
            System.out.println("left");
        }
        if (right){
            System.out.println("right");
            player.setVelocity(player.getVelocity().add(accel,0));
        }

        //Change player position based on velocity an deltaTime
        player.getView().setTranslateX(player.getView().getTranslateX() + player.getVelocity().getX() * deltaTime);
        player.getView().setTranslateY(player.getView().getTranslateY() + player.getVelocity().getY() * deltaTime);
    }

    @Override
    public void start(Stage stage){
        //Drawing canvas
        stage.setScene(new Scene(createContent()));

        /***********************************************************
         * Input handling
         ***********************************************************/
        stage.getScene().setOnKeyPressed(e ->{
            switch (e.getCode()){
                case W : forward = true; break;
                case S : backwards = true; break;
                case A : left = true; break;
                case D : right = true; break;
            }
        });

        stage.getScene().setOnKeyReleased(e->{
            switch (e.getCode()){
                case W : forward = false; break;
                case S : backwards = false; break;
                case A : left = false; break;
                case D : right = false; break;
            }
        });

        //Window properties
        stage.setTitle("GlowLine");
        stage.getScene().setFill(Color.BLACK);
        stage.setFullScreen(false);
        stage.setWidth(1920);
        stage.setHeight(1080);
        stage.show();

    }

    //Add object to game
    private void spawnGameObject(GameObject object, int x, int y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);

        root.getChildren().add(object.getView());
    }


    private void changeVelocity(GameObject object, double x, double y){
        object.setVelocity(object.getVelocity().add(x,y));
    }

    /********************Launch***********************/
    public static void main(String[] args){
        launch(args);
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public class Player extends GameObject {
        private int acceleration = 5;
        Player(){
            super(new Polygon(25,0 , 0,50 , 50,50), Color.WHEAT);
        }

        public int getAcceleration(){
            return acceleration;
        }
    }
}
