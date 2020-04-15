package Game;

import Game.Effects.Emitter;
import Game.Layout.SceneSetup;
import Game.Objects.Bullet;
import Game.Objects.GameObject;
import Game.Objects.Player;
import Game.Objects.Spawner;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ApplicationStart extends Application {
    protected static int resolutionX = 1920;
    protected static int resolutionY = 1080;
    private static boolean FULLSCREEN = false;

    //Root of scene
    private static final Pane root = new Pane();
    //Drawing canvas
    private static final Canvas canvas = new Canvas(resolutionX, resolutionY);
    private static final GraphicsContext gc = canvas.getGraphicsContext2D();

    //Scale for game objects 1920 by 1080 as base
    private static double scale = (double)resolutionY/1080;

    //References
    protected static AnimationTimer timer;
    private static Player player;
    private static List<GameObject> bullets = new ArrayList<>();
    private static List<GameObject> enemyBullets = new ArrayList<>();
    private static List<GameObject> enemies = new ArrayList<>();
    private static List<Emitter> emitters = new ArrayList<>();
    private static Emitter playerThrust;

    //Floor ref
    private static Rectangle floor = new Rectangle(0,resolutionY-20, resolutionX, 20);

    //input variables to be used in game loop, this is used to make motion/all actions smooth
    private static Boolean forward = false;
    private static Boolean shoot = false;
    static double mouseX = 0;
    static double mouseY = 0;


    /********************Enter point***********************/
    public static void main(String[] args) {
        initWindow();
        System.out.println(javafx.scene.text.Font.getFamilies());
    }

    //This is not used, manually started later. Override needed for the program to function.
    @Override
    public void start(Stage stage) {}

    /********************Resolution setup***********************/
    //Window JFrame initialization (This is needed to change the desktop resolution)
    private static void initWindow() {
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

    /********************Application start***********************/
    //Invoked on JavaFX thread, initiate JavaFX scene
    private static void initFX(JFXPanel fxPanel) {
        Scene mainMenu = SceneSetup.createMainMenu(fxPanel);
        Scene gameplay = SceneSetup.createGameplay(fxPanel, root);
        fxPanel.setDoubleBuffered(true);

        Scene controls = SceneSetup.createControls(fxPanel);
        fxPanel.setScene(mainMenu);

        /***********************************************************
         * Gameplay controls
         ***********************************************************/

        gameplay.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    forward = true;
                    break;
                case ESCAPE:
                    timer.stop();
                    root.getChildren().get(0).setVisible(true);
            }
        });

        gameplay.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W:
                    forward = false;
                    break;
            }
        });

        gameplay.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot = true;
            }
        });

        gameplay.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                shoot = false;
            }
        });

        gameplay.setOnMouseMoved(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });

        //For when mouse is clicked
        gameplay.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
    }

    //Setup for game related content, game loop, spawning, etc.
    public static void createRound() {
        root.getChildren().add(canvas);
        canvas.setMouseTransparent(true);
        canvas.setViewOrder(3);
        //root.setPrefSize(resolutionX, resolutionY);

        //Background (atmosphere)
        Node tempBackground;
        tempBackground = new Circle(0.5 * resolutionX,4.2 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.1));
        tempBackground.setMouseTransparent(true);
        tempBackground.setViewOrder(4);
        root.getChildren().add(tempBackground);

        tempBackground = new Circle(0.5 * resolutionX,4.6 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.15));
        tempBackground.setMouseTransparent(true);
        tempBackground.setViewOrder(4);
        root.getChildren().add(tempBackground);

        tempBackground = new Circle(0.5 * resolutionX,4.8 * resolutionY,4*resolutionY,
                Color.color(1,1,1, 0.135));
        tempBackground.setMouseTransparent(true);
        tempBackground.setViewOrder(4);
        root.getChildren().add(tempBackground);

        //floor
        floor.setFill(Color.color(1,1,1, 0.3));
        root.getChildren().add(floor);

        //Spawn Player
        player = new Player(scale);
        Spawner.spawnGameObject(player, resolutionX * 0.5, resolutionY * 0.5);
        playerThrust = new Emitter(30000, 2000, Color.hsb(300, 0.64, 0.76), Color.hsb(360, 1, 0.45),10, 0.15,"backwards", Math.PI/8, 1, 0.1,-1, player);


        /***********************************************************
         * Game Loop
         ***********************************************************/
        timer = new AnimationTimer() {
            long previousTime = 0;
            //Time since last frame in seconds
            double deltaTime;

            @Override
            public void handle(long now) {
                //now is in ns, convert to s
                deltaTime = (now - previousTime) * 0.000_000_001;
                previousTime = now;
                if (!(deltaTime > 1)){
                    update(deltaTime, now *  0.000_000_001);
                }
            }
        };
        timer.start();
    }

    /*****************Game Loop update**************************/
    private static double lastShot = 0;
    private static void update(double deltaTime, double time) {
//        System.out.println(time);
        //Collision handeling
        GameObject.Collision collision;

        //Spawn stuff
        Spawner.spawnPass(time);

        if(!player.isDead()) {
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
                Spawner.addGameObject(new Bullet(scale, "bullet"), player.getView()[0].getTranslateX(), player.getView()[0].getTranslateY());
                lastShot = time;
            }

            //TODO wrap collisions into functions
            //Player collision
            collision = player.getCollision(floor);
            if (collision.isCollided()) {
                double deltaY = collision.getY() - floor.getY();
                //For bounce
                //player.setVelocity(player.getVelocity().getX(), player.getVelocity().getY() - deltaY/deltaTime);
                //No bounce
                player.setVelocity(player.getVelocity().getX(), 0);
                player.getView()[0].setTranslateY(player.getView()[0].getTranslateY() - deltaY);
            }
            for (GameObject enemyBullet : enemyBullets) {
                collision = player.getCollision(enemyBullet);
                if (collision.isCollided()) {
                    removeGameObjectAll(enemyBullet, player);
                }
            }
        }

        //Lander collision
        for (GameObject lander : enemies){
            //floor
            collision = lander.getCollision(floor);
            if (collision.isCollided()){
                lander.setVelocity(0,0);
                System.exit(1);
            }
            //bullet
            for (GameObject bullet : bullets) {
                collision = lander.getCollision(bullet);
                if (collision.isCollided()){
                    removeGameObjectAll(lander, bullet);
                }
            }
        }

        //Enemy bullet collisions/clean
        for (GameObject enemyBullet : enemyBullets){
            if (enemyBullet.getType().equals("kamikaze")) {
                for (GameObject allyBullet : bullets){
                    collision = enemyBullet.getCollision(allyBullet);
                    if (collision.isCollided()){
                        removeGameObjectAll(enemyBullet, allyBullet);
                    }
                }

                //TODO add translate method tp game object to handle views, collision shape translation
                //Kamikaze floor collision
                collision = enemyBullet.getCollision(floor);
                if (collision.isCollided()) {
                    double deltaY = collision.getY() - floor.getY();
                    enemyBullet.setVelocity(enemyBullet.getVelocity().getX(), 0);
                    enemyBullet.getCollisionShape().setTranslateY(enemyBullet.getCollisionShape().getTranslateY() - deltaY);
                }
            }
        }

        //Bullet collisions/clean
        for (GameObject bullet : bullets) {
            collision = bullet.getCollision(floor);
            if (collision.isCollided()) {
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
        enemyBullets.removeIf(GameObject::isDead);

        gc.clearRect(0,0,resolutionX, resolutionY);
        //Update player, thrust emitter
        if(!player.isDead()) {
            player.update(deltaTime);
            if(forward) {
                playerThrust.emit(deltaTime);
            }
            playerThrust.update(deltaTime);
        }

        //Update emitters
        for (Iterator<Emitter> it = emitters.iterator(); it.hasNext();) {
            Emitter emitter = it.next();
            emitter.emit(deltaTime);
            emitter.update(deltaTime);

            if(emitter.isDead()){
                it.remove();
            }
        }

        //Update bullet positions
        for (GameObject bullet : bullets) {
            bullet.update(deltaTime);
        }

        //Update enemy bullet positions
        for (GameObject bullet : enemyBullets) {
            bullet.update(deltaTime);
        }

        //Update lander position
        for (GameObject lander : enemies){
            lander.update(deltaTime);
        }
    }

    private static void removeGameObject(GameObject object){
        object.setDead();
        for(Node view : object.getView()) {
            root.getChildren().remove(view);
        }
    }

    private static void removeGameObjectAll(GameObject... objects){
        for (GameObject object : objects) {
            object.setDead();

            for(Node view : object.getView()) {
                root.getChildren().remove(view);
            }
        }
    }

    //Getters
    public static GraphicsContext getGc() { return gc; }
    public static Pane getRoot(){ return root; }
    public static GameObject getPlayer(){ return player; }
    public static List<GameObject> getBullets(){ return bullets; }
    public static List<GameObject> getEnemies(){ return enemies; }
    public static List<GameObject> getEnemyBullets(){ return enemyBullets; }
    public static List<Emitter> getEmitters(){ return emitters; }
    public static double getMouseX(){ return mouseX; }
    public static double getMouseY(){ return mouseY; }
    public static double getScale() { return scale; }
    public static int getResolutionX() {return resolutionX; }
    public static int getResolutionY() {return resolutionY; }
}