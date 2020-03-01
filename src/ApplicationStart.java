import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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

    //This is not used, manually started later. Override needed for the program to function.
    @Override
    public void start(Stage stage){ return; }

    /********************Launch***********************/
    public static void main(String[] args){
        new ApplicationStart().initWindow();
    }

    //Window frame initialization (This is needed to change the desktop resolution)
    JFrame frame = new JFrame();
    private void initWindow() {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice devices = g.getScreenDevices()[0];

        frame = new JFrame();
        frame.setVisible(false);
        frame.getContentPane().setBackground(java.awt.Color.BLACK);

        //Revert to original resolution when app closes
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
                Window w = devices.getFullScreenWindow();
                if (w != null) {
                    w.dispose();
                }
                devices.setFullScreenWindow(null);
                frame.dispose();
                System.exit(0);
            }
        });

        devices.setFullScreenWindow(frame);
        DisplayMode oldMode = devices.getDisplayMode();
        DisplayMode displayMode = new DisplayMode(1920, 1080, oldMode.getBitDepth(), oldMode.getRefreshRate());
        devices.setDisplayMode(displayMode);

        //Used to embed FX into JFrame
        final JFXPanel fxPanel = new JFXPanel();

        frame.add(fxPanel);
        //Used to run initFX in the JavaFX thread
        Platform.runLater(() -> initFX(fxPanel));
    }

    //Invoked on JavaFX thread, initiate JavaFX
    private void initFX(JFXPanel fxPanel) {
        Scene scene = new Scene(createContent());
        fxPanel.setScene(scene);

        scene.setFill(Color.BLACK);

        /***********************************************************
         * Input handling
         ***********************************************************/
        scene.setOnKeyPressed(e ->{
            switch (e.getCode()){
                case W : forward = true; break;
                case S : backwards = true; break;
                case A : left = true; break;
                case D : right = true; break;
            }
        });

        scene.setOnKeyReleased(e->{
            switch (e.getCode()){
                case W : forward = false; break;
                case S : backwards = false; break;
                case A : left = false; break;
                case D : right = false; break;
            }
        });

        fxPanel.setVisible(true);
    }

    //Setup for game related content, game loop, spawning, etc.
    private Parent createContent(){
        root.setPrefSize(1920,1080);

        //Spawn Player
        spawnGameObject(player,960, 900);

        /***********************************************************
         * Game Loop
         ***********************************************************/

        AnimationTimer timer = new AnimationTimer() {
            long previousTime = 0;
            //Time since last frame
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
        double accel = player.getAcceleration() * deltaTime;
        if (forward){
            accelerate(player,0,-accel);
        }
        if (backwards){
            accelerate(player,0,accel);
        }
        if (left){
            accelerate(player,-accel,0);
        }
        if (right){
            accelerate(player,accel,0);
        }

        //Change player position based on velocity an deltaTime
        player.getView().setTranslateX(player.getView().getTranslateX() + player.getVelocity().getX() * deltaTime);
        player.getView().setTranslateY(player.getView().getTranslateY() + player.getVelocity().getY() * deltaTime);
    }

    //Add object to game
    private void spawnGameObject(GameObject object, int x, int y){
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);

        root.getChildren().add(object.getView());
    }


    private void accelerate(GameObject object, double x, double y){
        object.setVelocity(object.getVelocity().add(x,y));
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public class Player extends GameObject {
        //px/s
        private int acceleration = 300;
        Player(){
            super(new Polygon(25,0 , 0,50 , 50,50), Color.WHEAT);
        }

        public int getAcceleration(){
            return acceleration;
        }
    }
}
