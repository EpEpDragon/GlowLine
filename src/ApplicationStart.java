import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import java.util.ArrayList;
import java.util.List;


public class ApplicationStart extends Application {
    //Root of scene
    final private Pane root = new Pane();

    final private int[] resolution = {1920, 1080};
    final private boolean FULLSCREEN = true;

    //Player ref
    final private Player player = new Player();
    private List<GameObject> bullets = new ArrayList<>();

    //input variables to be used in game loop, this is used to make motion/all actions smooth
    Boolean forward = false;
    //    Boolean backwards = false;
//    Boolean left = false;
//    Boolean right = false;
    Boolean shoot = false;
    double[] mousePos = {0,0};

    /********************Launch***********************/
    public static void main(String[] args){
        new ApplicationStart().initWindow();
    }

    //This is not used, manually started later. Override needed for the program to function.
    @Override
    public void start(Stage stage){ return; }

    //Window JFrame initialization (This is needed to change the desktop resolution)
    private void initWindow() {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice devices = g.getScreenDevices()[0];
        //This is the application window
        final JFrame frame = new JFrame("GlowLine");
        frame.getContentPane().setBackground(java.awt.Color.BLACK);
        frame.setSize(resolution[0],resolution[1]);
        frame.setUndecorated(true);
        frame.setResizable(false);
        frame.setVisible(true);

        //Revert to original resolution when app closes
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                devices.setFullScreenWindow(null);
                frame.dispose();
                System.exit(0);
            }
        });

        if(FULLSCREEN) {
            //This changes the PC's resolution
            devices.setFullScreenWindow(frame);
            DisplayMode oldMode = devices.getDisplayMode();
            DisplayMode newDisplayMode = new DisplayMode(resolution[0], resolution[1], oldMode.getBitDepth(), oldMode.getRefreshRate());
            devices.setDisplayMode(newDisplayMode);
        }

        //JFXPanel is used to embed a JavaFX element into a JFrame
        final JFXPanel fxPanel = new JFXPanel();
        frame.add(fxPanel);

        //Run initFX in the JavaFX thread
        Platform.runLater(() -> initFX(fxPanel));
    }

    //Invoked on JavaFX thread, initiate JavaFX scene
    private void initFX(JFXPanel fxPanel) {
        Scene scene = new Scene(createContent());
        fxPanel.setScene(scene);

        scene.setFill(Color.BLACK);
        scene.setCursor(Cursor.CROSSHAIR);

        /***********************************************************
         * Input handling
         ***********************************************************/
        scene.setOnKeyPressed(e ->{
            switch (e.getCode()){
                case W : forward = true; break;
//                case S : backwards = true; break;
//                case A : left = true; break;
//                case D : right = true; break;
            }
        });

        scene.setOnKeyReleased(e->{
            switch (e.getCode()){
                case W : forward = false; break;
//                case S : backwards = false; break;
//                case A : left = false; break;
//                case D : right = false; break;
            }
        });


        scene.setOnMousePressed(e->{
            if(e.getButton() == MouseButton.PRIMARY){
                shoot = true;
            }
        });

        scene.setOnMouseReleased(e->{
            if(e.getButton() == MouseButton.PRIMARY){
                shoot = false;
            }
        });

        scene.setOnMouseMoved(e->{
            mousePos[0] = e.getX();
            mousePos[1] = e.getY();
        });

        //For when mouse is clicked
        scene.setOnMouseDragged(e->{
            mousePos[0] = e.getX();
            mousePos[1] = e.getY();
        });

        fxPanel.setVisible(true);
    }

    //Setup for game related content, game loop, spawning, etc.
    private Parent createContent(){
        root.setPrefSize(resolution[0],resolution[1]);

        //Spawn Player
        spawnGameObject(player,resolution[0]*0.5, resolution[1]*0.8);

        /***********************************************************
         * Game Loop
         ***********************************************************/
        AnimationTimer timer = new AnimationTimer() {
            long previousTime = 0;
            //Time since last frame in seconds
            double deltaT;

            @Override
            public void handle(long now) {
                //now is in ns, convert to s
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
        double accel = player.getAcceleration() * deltaTime;
        if (forward){
            accelerate(player, player.getForwardVector().multiply(player.getAcceleration()));
        }
//        if (backwards){
//            accelerate(player,0,accel);
//        }
//        if (left){
//            accelerate(player,-accel,0);
//        }
//        if (right){
//            accelerate(player,accel,0);
//        }

        if (shoot){
            addGameObject(new Bullet(), "bullet", player.getView().getTranslateX(), player.getView().getTranslateY());
        }

        //Update player rotation
        player.setRotation(VecMath.deltaAngle(player.getView().getTranslateX(), player.getView().getTranslateY(), mousePos[0], mousePos[1]));

        updatePosition(player, deltaTime);

        //Update bullet positions
        for (int i = 0; i < bullets.size(); i++) {
            updatePosition(bullets.get(i), deltaTime);
        }
    }

    private void addGameObject(GameObject object, String type, double x, double y){
        spawnGameObject(object, x, y);
        System.out.println(object.getVelocity());
        switch (type){
            case "bullet": bullets.add(object); break;
        }
    }
    private void spawnGameObject(GameObject object, double x, double y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);

        root.getChildren().add(object.getView());
    }

    private void accelerate(GameObject object, Point2D acceleration){
        object.setVelocity(object.getVelocity().add(acceleration));
    }

    private void updatePosition(GameObject object, double deltaTime){
        object.getView().setTranslateX(object.getView().getTranslateX() + object.getVelocity().getX() * deltaTime);
        object.getView().setTranslateY(object.getView().getTranslateY() + object.getVelocity().getY() * deltaTime);
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public class Player extends GameObject {
        //px/s
        private int acceleration = 10;
        Player(){
            super(new Polygon(25,0 , -25,25 , -25,-25),300, Color.WHEAT);
        }

        public int getAcceleration(){
            return acceleration;
        }
    }

    public class Bullet extends GameObject {
        private int lifetime = 1;
        double speed = 300;
        Bullet(){
            super(new Circle(10,Color.BURLYWOOD),600);
            setVelocity(player.getForwardVector().multiply(speed).add(player.getVelocity()));
        }

    }
}