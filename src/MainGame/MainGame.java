package MainGame;

import MainGame.Effects.Emitter;
import MainGame.Layout.SceneSetup;
import MainGame.Objects.*;
import javafx.animation.ScaleTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static MainGame.Layout.SceneSetup.*;
import static MainGame.Objects.gameStateHandler.*;

public class MainGame extends Application {
    static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    private static boolean FULLSCREEN = false;
    private static int resolutionX = gd.getDisplayMode().getWidth();
    private static int resolutionY = gd.getDisplayMode().getHeight();
//    protected static int resolutionX = 1360;
//    protected static int resolutionY = 768;

    //Root of scene
    private static final Pane root = new Pane();
    public static final Pane gameplayElements = new Pane();

    //Drawing canvas
    private static final Canvas canvas = new Canvas(resolutionX, resolutionY);
    private static final GraphicsContext gc = canvas.getGraphicsContext2D();

    //Scale for game objects 1920 by 1080 as base
    private static double scale = (double) resolutionY / 1080;

    //References
    protected static StatusTimer timer;
    private static Player player;
    private static List<GameObject> bullets = new ArrayList<>();
    private static List<GameObject> enemyBullets = new ArrayList<>();
    private static List<GameObject> enemies = new ArrayList<>();
    private static List<Emitter> emitters = new ArrayList<>();
    private static Emitter playerThrust;

    //Floor ref
    private static Rectangle floor = new Rectangle(0, resolutionY - 20, resolutionX, 20);
    private static Rectangle ceiling = new Rectangle(0, 0, resolutionX, 30);

    //input variables to be used in game loop, this is used to make motion/all actions smooth
    private static Boolean forward = false;
    private static Boolean left = false;
    private static Boolean right = false;
    private static Boolean shoot = false;
    private static Boolean space = false;
    static double mouseX = 0;
    static double mouseY = 0;
    private static double currentTime;

    /********************Enter point***********************/
    public static void main(String[] args) {
        initWindow();
    }

    //This is not used, manually started later. Override needed for the program to function.
    @Override
    public void start(Stage stage) {
    }

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
        //get mainMenu background song to play early
        playMainMenuSong();

        Scene mainMenu = SceneSetup.createMainMenu(fxPanel);
        Scene controls = SceneSetup.createControls(fxPanel);
        Scene gameplay = SceneSetup.createGameplay(fxPanel, root);

        fxPanel.setScene(mainMenu);
        fxPanel.setDoubleBuffered(true);
        printHighScores();

        /***********************************************************
         Gameplay controls
         ***********************************************************/

        gameplay.setOnKeyPressed(e -> {
            switch (e.getCode()) {
                case W:
                    forward = true;
                    break;
                case A:
                    left = true;
                    break;
                case D:
                    right = true;
                    break;
                case SPACE:
                    space = true;
                    break;
                case Q:
                    if (timer != null) {
                        if (timer.isRunning() || root.getChildren().get(11).isVisible()) {
                            stopGamePlaySongs();
                            startMainMenuSong();
                            timer.stop();
                            gameplayElements.getChildren().remove(0, gameplayElements.getChildren().size());
                            SceneSetup.clearStuff();
                            fxPanel.setScene(mainMenu);
                            playAll((Pane) mainMenu.getRoot());
                            if (root.getChildren().get(9).isVisible()) {
                                setGameOverVisible(false);
                                saveScore();
                            }
                            if (root.getChildren().get(11).isVisible()) {
                                root.getChildren().get(11).setVisible(false);
                            }
                        }
                    }
                    break;
                case ESCAPE:
                    if (root.getChildren().get(11).isVisible() && !getGameOverState()) {
                        if (SceneSetup.isReadyToResume()) {
                            timer.start();
                            root.getChildren().get(11).setVisible(false);
                        }
                    } else if (timer != null && !space) { //!space is necessary to prevent escape from working while time dilating, because this causes bug...
                        if (!getGameOverState() && timer.isRunning()) {
                            SceneSetup.setReadyToResume(false);
                            timer.stop();
                            root.getChildren().get(11).setVisible(true);
                            playAll((Pane) gameplay.getRoot());
                        }
                    }
                    break;
            }
        });

        gameplay.setOnKeyReleased(e -> {
            switch (e.getCode()) {
                case W:
                    forward = false;
                    break;
                case A:
                    left = false;
                    break;
                case D:
                    right = false;
                    break;
                case SPACE:
                    space = false;
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

        //For when mouse is held down
        gameplay.setOnMouseDragged(e -> {
            mouseX = e.getX();
            mouseY = e.getY();
        });
    }

    //Setup for game related content, game loop, spawning, etc. To be called at all begginings of games i.e. when restarting
    public static void createRound() {
        gc.clearRect(0, 0, resolutionX, resolutionY);
        gameplayElements.getChildren().add(canvas);
        canvas.setMouseTransparent(true);

        space = false;
        shoot = false;

        gameStateSetup();

        //mouse info given in case mouse not moved from when clicking start game till start animation finished and thus mouse info still refers to 0,0
        mouseX = MouseInfo.getPointerInfo().getLocation().x;
        mouseY = MouseInfo.getPointerInfo().getLocation().y;

        Spawner.spawnBackground();

        //add floor
        floor.setFill(Color.color(1, 1, 1, 0.3));
        gameplayElements.getChildren().add(floor);

        //Spawn Player
        player = new Player(scale);
        Spawner.spawnGameObject(player, resolutionX * 0.5, resolutionY * 0.95);
        player.setRotation(-Math.PI / 2);

        //Starting animation
        ScaleTransition startAnim = new ScaleTransition(Duration.seconds(2), player.getView()[0]);
        startAnim.setCycleCount(1);

        startAnim.setFromY(200);
        startAnim.setFromX(200);
        startAnim.setToX(1);
        startAnim.setToY(1);

        startAnim.play();
        startAnim.setOnFinished(event -> {
            playerThrust = new Emitter(30000, 2000, Color.hsb(360, 0.64, 0.76), Color.hsb(300, 1, 0.45), 10, 0.15, "backwards", Math.PI / 8, 1, 0.1, -1, player);

            /***********************************************************
             * Game Loop
             ***********************************************************/
            currentTime = 0;
            timer = new StatusTimer() {
                long previousTime = 0;
                //Time since last frame in seconds
                double deltaTime;

                @Override
                public void handle(long now) {
                    //now is in ns, convert to s
                    deltaTime = (now - previousTime) * getTimeSpeed();
                    previousTime = now;
                    //prevents massive deltaTime's from going through e.g. at start-up deltaTime is very large
                    if (!(deltaTime > 1)) {
                        currentTime += deltaTime;
                        update(deltaTime, currentTime);
                    }
                }
            };
            timer.start();
        });
    }

    /*****************Game Loop update**************************/
    private static void update(double deltaTime, double time) {
        //set level
        levelManagement(time);

        //Update lives left indicator
        updateLives();

        //Spawn necessary stuff
        Spawner.spawnPass(time);

        if (!player.isDead()) {
            //make player move
            playerAccelerate(deltaTime);

            //shooter update
            shootPass(time);

            // Show colour animation when updating score
            showScoreUpdate(time);
        }

        //Remove dead things
        enemies.removeIf(GameObject::isDead);
        bullets.removeIf(GameObject::isDead);
        enemyBullets.removeIf(GameObject::isDead);

        gc.clearRect(0, 0, resolutionX, resolutionY);

        //Update player, thrust emitter
        if (!player.isDead()) {
            player.update(deltaTime);
            if ((forward || getForceThrust()) && !(getLevel() == 1)) {
                playerThrust.emit(deltaTime);
            }
            playerThrust.update(deltaTime);
        }

        //Update emitters
        for (Iterator<Emitter> it = emitters.iterator(); it.hasNext(); ) {
            Emitter emitter = it.next();
            emitter.emit(deltaTime);
            emitter.update(deltaTime);

            if (emitter.isDead()) {
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
        for (GameObject lander : enemies) {
            lander.update(deltaTime);
        }

        //(HUD is timer)
        updateHUDandScore(time);

        //(NB to be last in update, otherwise update method continues with current time variable after the restart executed)
        if (getGameOverState()) {
            gameOverPass(time);
        } else {
            timeDilationPass(time);
        }
    }

    //Getters
    public static boolean getSpace() {
        return space;
    }

    public static boolean getShoot() {
        return shoot;
    }

    public static boolean getForward() {
        return forward;
    }

    public static boolean getLeft() { return left; }

    public static boolean getRight() {
        return right;
    }

    public static GraphicsContext getGc() {
        return gc;
    }

    public static Pane getRoot() {
        return root;
    }

    public static Pane getGameplayElements() {
        return gameplayElements;
    }

    public static Player getPlayer() {
        return player;
    }

    public static List<GameObject> getBullets() {
        return bullets;
    }

    public static List<GameObject> getEnemies() {
        return enemies;
    }

    public static List<GameObject> getEnemyBullets() {
        return enemyBullets;
    }

    public static List<Emitter> getEmitters() {
        return emitters;
    }

    public static double getMouseX() {
        return mouseX;
    }

    public static double getMouseY() {
        return mouseY;
    }

    public static double getScale() {
        return scale;
    }

    public static int getResolutionX() {
        return resolutionX;
    }

    public static int getResolutionY() {
        return resolutionY;
    }

    public static Rectangle getFloor() {
        return floor;
    }

    public static Rectangle getCeiling() {
        return ceiling;
    }

    public static StatusTimer getTimer() {
        return timer;
    }

    public static double getCurrentTime() {
        return currentTime;
    }
}