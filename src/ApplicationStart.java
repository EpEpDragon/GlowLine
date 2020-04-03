import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;


import java.util.ArrayList;
import java.util.List;

public class ApplicationStart extends Application {
    //Root of scene
    static final private Pane root = new Pane();

    static private int resolutionX = 1920;
    static private int resolutionY = 1080;
    static private boolean FULLSCREEN = false;

    //Scale for game objects 1920 by 1080 as base
    static double scale = (double)resolutionY/1080;

    static Spawner spawner = new Spawner(resolutionX, resolutionY);

    //References
    static private Spawner.Player player = new Spawner.Player(scale);
    static private List<GameObject> bullets = new ArrayList<>();
    static private List<GameObject> enemies = new ArrayList<>();

    //Floor ref
    Rectangle floor = new Rectangle(0,resolutionY-20, resolutionX, 20);

    //input variables to be used in game loop, this is used to make motion/all actions smooth
    Boolean forward = false;
    //    Boolean backwards = false;
    //    Boolean left = false;
    //    Boolean right = false;
    Boolean shoot = false;
    static double mouseX = 0;
    static double mouseY = 0;


    /********************Launch***********************/
    public static void main(String[] args) {
        new ApplicationStart().initWindow();
    }

    //This is not used, manually started later. Override needed for the program to function.
    @Override
    public void start(Stage stage) {
        return;
    }

    //Window JFrame initialization (This is needed to change the desktop resolution)
    private void initWindow() {
        GraphicsEnvironment g = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice devices = g.getScreenDevices()[0];
        //This is the application window
        final JFrame frame = new JFrame("GlowLine");
        frame.getContentPane().setBackground(java.awt.Color.BLACK);
        frame.setSize(resolutionX, resolutionY);
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

        if (FULLSCREEN) {
            //This changes the PC's resolution
            devices.setFullScreenWindow(frame);
            DisplayMode newDisplayMode = new DisplayMode(resolutionX, resolutionY,
                    devices.getDisplayMode().getBitDepth(), devices.getDisplayMode().getRefreshRate());

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
        scene.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    forward = true;
                    break;
//                case S:
//                    backwards = true;
//                    break;
//                case A:
//                    left = true;
//                    break;
//                case D:
//                    right = true;
//                    break;
            }
        });

        scene.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W:
                    forward = false;
                    break;
//                case S:
//                    backwards = false;
//                    break;
//                case A:
//                    left = false;
//                    break;
//                case D:
//                    right = false;
//                    break;
            }
        });


        scene.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot = true;
            }
        });

        scene.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot = false;
            }
        });

        scene.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        //For when mouse is clicked
        scene.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        fxPanel.setVisible(true);
    }

    //Setup for game related content, game loop, spawning, etc.
    private Parent createContent() {
        root.setPrefSize(resolutionX, resolutionY);

        //Background (atmosphere)
        root.getChildren().add(new Circle(0.5 * resolutionX,4.2 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.1)));
        root.getChildren().add(new Circle(0.5 * resolutionX,4.6 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.15)));
        root.getChildren().add(new Circle(0.5 * resolutionX,4.8 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.135)));

        //floor
        floor.setFill(Color.color(1,1,1, 0.3));
        root.getChildren().add(floor);

        //Spawn Player
        spawner.spawnGameObject(player, resolutionX * 0.5, resolutionY * 0.5);
        player.setVelocity(0,-300);
        Spawner.addGameObject(new Spawner.Kamikaze(scale), "enemy", resolutionX*0.5, resolutionY*0.1);
        enemies.get(0).setVelocity(0,0);
//        Point2D interceptVec = OwnMath.findInterceptVector(new Point2D(enemies.get(0).getView()[0].getTranslateX(), enemies.get(0).getView()[0].getTranslateY()), new Point2D(getPlayer().getView()[0].getTranslateX(), getPlayer().getView()[0].getTranslateY()), enemies.get(0).getVelocity(), getPlayer().getVelocity(), enemies.get(0).getMaxVelocity()).normalize();
//        enemies.get(0).accelerate(interceptVec.multiply(1000));



        /***********************************************************
         * Game Loop
         ***********************************************************/
        AnimationTimer timer = new AnimationTimer() {
            long previousTime = 0;
            //Time since last frame in seconds
            double deltaTime;

            @Override
            public void handle(long now) {
                //now is in ns, convert to s
                deltaTime = (now - previousTime) * 0.000_000_001;
                if (deltaTime > 1){deltaTime = 0;}
                previousTime = now;
                update(deltaTime, now * 0.000_000_001);
            }
        };
        timer.start();

        return root;
    }

    /***********************************************************
     * Game Loop update
     ***********************************************************/
    double lastShot = 0;

    private void update(double deltaTime, double time) {
        //System.out.println(deltaTime);

        //Spawn stuff
        spawner.spawnPass(time);

        //Accelerate player
        double playerAcceleration = player.getAcceleration() * deltaTime;
        if (forward) {
            player.accelerate(player.getForwardVector().multiply(playerAcceleration));
        }

//        if (backwards) {
//            player.accelerate(player.getForwardVector().multiply(playerAcceleration * -1));
//        }
//
//        if (left) {
//            accelerate(player, player.getForwardVector().multiply(player.getAcceleration()));
//        }
//        if (right) {
//            accelerate(player, accel, 0);
//        }

        if (shoot && time - lastShot > 0.5) {
            //System.out.println(time-lastShot);
            spawner.addGameObject(new Spawner.Bullet(scale), "bullet", player.getView()[0].getTranslateX(), player.getView()[0].getTranslateY());
            lastShot = time;
        }

        //Player collision
        GameObject.Collision collision = player.getCollision(floor);
        if (collision.collided){
            double deltaY = collision.y - floor.getY();
            //For bounce
            //player.setVelocity(player.getVelocity().getX(), player.getVelocity().getY() - deltaY/deltaTime);
            //No bounce
            player.setVelocity(player.getVelocity().getX(), 0);
            player.getView()[0].setTranslateY(player.getView()[0].getTranslateY() - deltaY);
        }

        //Lander collision
        for (GameObject lander : enemies){
            //floor
            collision = lander.getCollision(floor);
            if (collision.collided){
                lander.setVelocity(0,0);
                System.exit(1);
            }
            //bullet
            for (GameObject bullet : bullets) {
                collision = lander.getCollision(bullet);
                if (collision.collided){
                    removeGameObjectAll(lander, bullet);
                }
            }
        }

        //Bullet clean
        for (GameObject bullet : bullets) {
            collision = bullet.getCollision(floor);
            if (collision.collided) {
                removeGameObject(bullet);
            }

            if ((bullet.getView()[0].getTranslateX() < 0 || bullet.getView()[0].getTranslateX() > resolutionX) ||
                    (bullet.getView()[0].getTranslateY() < 0 || bullet.getView()[0].getTranslateY() > resolutionY)){
                removeGameObject(bullet);
            }
        }

        //Remove dead things
        enemies.removeIf(GameObject::isDead);
        bullets.removeIf(GameObject::isDead);

        //Update player
        player.update(deltaTime);

        //Update bullet positions
        for (GameObject bullet : bullets) {
            bullet.update(deltaTime);
        }

        //Update lander position
        for (GameObject lander : enemies){
            lander.update(deltaTime);
        }
    }

    private void removeGameObject(GameObject object){
        object.setDead(true);
        for(Node view : object.getView()) {
            root.getChildren().remove(view);
        }
    }

    private void removeGameObjectAll(GameObject... objects){
        for (GameObject object : objects) {
            object.setDead(true);

            for(Node view : object.getView()) {
                root.getChildren().remove(view);
            }
        }
    }

    //Getters
    public static Pane getRoot(){ return root; }
    public static GameObject getPlayer(){ return player; }
    public static List<GameObject> getBullets(){ return bullets; }
    public static List<GameObject> getEnemies(){ return enemies; }
    public static double getMouseX(){ return mouseX; }
    public static double getMouseY(){ return mouseY; }
}