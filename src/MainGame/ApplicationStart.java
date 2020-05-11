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
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.scene.canvas.Canvas;
import javafx.util.Duration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static MainGame.Layout.SceneSetup.*;
import static MainGame.Math.OwnMath.colorLerp;
import static MainGame.Math.OwnMath.getPlaceValue;
import static MainGame.Objects.Spawner.*;

public class ApplicationStart extends Application {
    static GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();

    private static boolean FULLSCREEN = false;
    protected static int resolutionX = gd.getDisplayMode().getWidth();
    protected static int resolutionY = gd.getDisplayMode().getHeight();
//    protected static int resolutionX = 1360;
//    protected static int resolutionY = 768;

    //File locations
    protected static String highScoreFileName = "src/MainGame/highScores.txt";
    protected static String mainMenuSongFile = "src/MainGame/AudioFiles/mainMenu.mp3";
    protected static String shooterFile = "src/MainGame/AudioFiles/shooter.mp3";
    protected static String deadFile = "src/MainGame/AudioFiles/dead.mp3";
    protected static String screamFile = "src/MainGame/AudioFiles/scream.mp3";
    protected static String explosionFile = "src/MainGame/AudioFiles/explosion.mp3";
    protected static String gameplaySongFile = "src/MainGame/AudioFiles/cantina.mp3";
    protected static String level2SongFile = "src/MainGame/AudioFiles/level2.mp3";

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
    private static Rectangle ceiling = new Rectangle(0,0, resolutionX, 30);

    //input variables to be used in game loop, this is used to make motion/all actions smooth
    private static Boolean forward = false;
    private static Boolean left = false;
    private static Boolean right = false;
    private static Boolean shoot = false;
    private static Boolean space = false;
    static double mouseX = 0;
    static double mouseY = 0;

    private static double currentTime;
    private static double timeSpeed;
    private static int enemiesKillCount;
    private static int level;
    private static int livesLeft;
    private static boolean gameOverState;
    private static double gameOverSince;
    private static int currentScore;
    private static double lastShot;
    private static double lastScoreUpdate;
    private static double lastLanderUpdate;
    private static boolean enteringName;
    private static double gameDifficulty = 1;
    private static double originalDifficulty = 1;
    private static double timeOfLevel2;
    private static boolean savedScore;
    private static boolean forceThrust;

    //time Dilation variables...
    private static double timeDilationLastUpdate;
    private static double timeOfPenalty;
    private static double timeDilationLeft;
    final static double timeDilationMax = 0.8;
    final static double penaltyTime = 1.6;

    //audio files
    private static AudioClip mainMenuSong = new AudioClip(new File(mainMenuSongFile).toURI().toString());
    private static AudioClip shooterSound = new AudioClip(new File(shooterFile).toURI().toString());
    private static AudioClip deadSound = new AudioClip(new File(deadFile).toURI().toString());
    private static AudioClip screamSound = new AudioClip(new File(screamFile).toURI().toString());
    private static AudioClip explosionSound = new AudioClip(new File(explosionFile).toURI().toString());
    private static AudioClip gameplaySong = new AudioClip(new File(gameplaySongFile).toURI().toString());
    private static AudioClip level2Song = new AudioClip(new File(level2SongFile).toURI().toString());


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
        System.out.println(resolutionX);
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
        mainMenuSong.play();

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
                        if (timer.isRunning() || root.getChildren().get(10).isVisible()) {
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
                            if (root.getChildren().get(10).isVisible()) {
                                root.getChildren().get(10).setVisible(false);
                            }
                        }
                    }
                    break;
                case ESCAPE:
                    if (root.getChildren().get(10).isVisible() && !gameOverState) {
                        if (SceneSetup.isReadyToResume()) {
                            timer.start();
                            root.getChildren().get(10).setVisible(false);
                        }
                    } else if (timer != null && !space){ //!space is necessary to prevent escape from working while time dilating, because this causes bug...
                        if (!gameOverState && timer.isRunning()) {
                            SceneSetup.setReadyToResume(false);
                            timer.stop();
                            root.getChildren().get(10).setVisible(true);
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

    //Setup for game related content, game loop, spawning, etc.
    public static void createRound() {
        gameplaySong.play();
        gc.clearRect(0, 0, resolutionX, resolutionY);
        gameplayElements.getChildren().add(canvas);
        canvas.setMouseTransparent(true);

        setLives(0);
        setTimeDilationFraction(0);
        gameDifficulty = originalDifficulty;
        savedScore = false;
        livesLeft = 3;
        lastShot = 0;
        timeDilationLeft = 0;
        timeOfPenalty = 0;
        timeDilationLastUpdate = 0;
        timeOfLevel2 = -1;
        space = false;
        enteringName = false;
        enemiesKillCount = 0;
        level = 1;
        forceThrust = false;
        resetSpawner();
        timeSpeed = 0.000_000_001;
        gameOverState = false;
        gameOverSince = -1;
        currentScore = 0;
        lastScoreUpdate = -10;
        lastLanderUpdate = -10;
        resetNameEnter();

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

        //Start animation
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
                    deltaTime = (now - previousTime) * timeSpeed;
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
        if (enemiesKillCount >= getL1EnemyCount()) {
            if (level == 1)
            {
                gameplaySong.stop();
                level2Song.play();
                timeOfLevel2 = time;
            }
            level = 2;
            if (time-timeOfLevel2 > 5) {
                double difficultyChangeRate = 1.0/(getOriginalDifficulty()*60); //1 difficulty per 60 seconds if difficulty level set was 1
                gameDifficulty = getOriginalDifficulty() + (time-timeOfLevel2-5)*difficultyChangeRate;
            }
            forceThrust = time - timeOfLevel2 < 0.05;
        }

        setLives(livesLeft);

        //Spawn stuff
        Spawner.spawnPass(time);

        if (!player.isDead()) {
            //Accelerate player
            double playerAcceleration = player.getAcceleration() * deltaTime;

            // Level 2+
            if (getLevel() > 1) {
                player.setMaxVelocity(scale*getDifficulty()*400);
                if (forward || forceThrust) {
                    player.accelerate(player.getForwardVector().multiply(playerAcceleration));
                }
            }
            // Level 1
            else {
                player.setMaxVelocity(scale*getDifficulty()*300);
                if (left) {
                    player.accelerate(-playerAcceleration, 0);
                }
                if (right) {
                    player.accelerate(playerAcceleration, 0);
                }
                if (!(left || right)) {
                    // if moving right faster than 15
                    if (player.getVelocity().getX() > 15) {
                        player.accelerate(-playerAcceleration, 0);
                    }
                    // if moving left faster than 15
                    else if (player.getVelocity().getX() < -15) {
                        player.accelerate(playerAcceleration, 0);
                    }
                    //if moving close to zero
                    else {
                        player.setVelocity(0, 0);
                    }
                }
            }

            if (shoot && time - lastShot > Player.getRechargeTime() && !gameOverState) {
                shooterSound.play();
                Spawner.addGameObject(new Bullet(scale, "bullet"), player.getView()[0].getTranslateX(), player.getView()[0].getTranslateY());
                lastShot = time;
            }

            // Show score update
            double timeUpdateTime = 0.3;
            if ((time - lastScoreUpdate) / timeUpdateTime > 1) {
                SceneSetup.updateScoreColour(Color.WHITE);
            } else {
                SceneSetup.updateScoreColour(colorLerp(Color.DARKBLUE, Color.LIGHTGREEN, (time - lastScoreUpdate) / timeUpdateTime));
            }
        }

        //Remove dead things
        enemies.removeIf(GameObject::isDead);
        bullets.removeIf(GameObject::isDead);
        enemyBullets.removeIf(GameObject::isDead);

        gc.clearRect(0, 0, resolutionX, resolutionY);
        //Update player, thrust emitter
        if (!player.isDead()) {
            player.update(deltaTime);
            if ((forward || forceThrust) && !(getLevel() == 1)) {
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

        //Update HUD
        int min = (int) time / 60;
        int sec = (int) (time - (int) (time / 60) * 60);
        String timeString = Integer.toString(getPlaceValue(min, 10)) + getPlaceValue(min, 1) + ":" + getPlaceValue(sec, 10) + getPlaceValue(sec, 1);
        SceneSetup.updateTime(timeString);
        //Update score
        SceneSetup.updateScore("Score: " + currentScore);



        //(NB to be last in update, otherwise update method continues with current time variable after the restart executed)
        //If gameover state was triggered somewhere during the current update
        if (gameOverState) {
            //store the earliest time that gameover was detected
            if (gameOverSince == -1) {
                gameOverSince = time;
                deadSound.play();
            }
            root.getChildren().get(8).setVisible(false);
            if (!enteringName) {
                double secSinceGameOver = (time - gameOverSince) / (timeSpeed * 1000000000);
                int secBeforeRestart = 10;
                if (secSinceGameOver > secBeforeRestart) {
                    saveScore();
                    setGameOverVisible(false);
                    gameplayElements.getChildren().remove(0, gameplayElements.getChildren().size());
                    gameOverState = false;
                    SceneSetup.clearStuff();
                    //resets the time and score before the starting animation begins.
                    resetHUD();
                    createRound();
                    timer.stop();
                } else {
                    updateRestartBtn("Restart (" + (int) (secBeforeRestart - secSinceGameOver + 1) + ")");
                }
            } else {
                updateRestartBtn("Restart");
            }
        } else {
            //speed is relative to 1
            double increaseSpeed = 0.2;
            double decreaseSpeed = 1;

            //time dilation timer
            //if still in penalty
            if (time-timeOfPenalty<penaltyTime){
                setTimeDilated(false);
                timeDilationLeft += increaseSpeed * (time - timeDilationLastUpdate);
                setTimeDilationColor(Color.INDIANRED);
            } else {
                setTimeDilationColor(Color.LIGHTGREEN);
                if (space) {
                    setTimeDilated(true);
                    timeDilationLeft -= decreaseSpeed * (time - timeDilationLastUpdate);
                } else {
                     setTimeDilated(false);
                     if (timeDilationLeft + time - timeDilationLastUpdate>timeDilationMax) {
                         timeDilationLeft = timeDilationMax;
                     } else {
                         timeDilationLeft += increaseSpeed * (time - timeDilationLastUpdate);
                     }
                }
                if (timeDilationLeft < 0.01) {
                    timeOfPenalty = time;
                }
            }
            setTimeDilationFraction(timeDilationLeft/timeDilationMax);
            timeDilationLastUpdate = time;
        }
    }

    public static void lostLife(){
        livesLeft -= 1;
        screamSound.play();
        enemiesKillCount += 1;
    }

    public static void gameOver() {
        gameplaySong.stop();
        level2Song.stop();
        setGameOverVisible(true);
        timeSpeed = 0.000_000_0001;
        gameOverState = true;
    }

    private static void setTimeDilated(boolean timeDilated){
        if (timeDilated){
            timeSpeed = 0.000_000_0003;
        } else {
            timeSpeed = 0.000_000_001;
        }
        root.getChildren().get(8).setVisible(timeDilated);
    }

    public static void setGameOverVisible(boolean visible){
        //set gameOverMenu to visible value
        root.getChildren().get(9).setVisible(visible);
        //set gameOver Darken Rectangle to visible value
        root.getChildren().get(7).setVisible(visible);
    }

    public static void setSavedScore(boolean saved){
        savedScore = saved;
    }

    private static void setLives(int livesLeft){
        switch (livesLeft) {
            case 3:
                root.getChildren().get(4).setVisible(true);
                root.getChildren().get(5).setVisible(true);
                root.getChildren().get(6).setVisible(true);
                break;
            case 2:
                root.getChildren().get(4).setVisible(true);
                root.getChildren().get(5).setVisible(true);
                root.getChildren().get(6).setVisible(false);
                break;
            case 1:
                root.getChildren().get(4).setVisible(true);
                root.getChildren().get(5).setVisible(false);
                root.getChildren().get(6).setVisible(false);
                break;
            case 0:
                root.getChildren().get(4).setVisible(false);
                root.getChildren().get(5).setVisible(false);
                root.getChildren().get(6).setVisible(false);
                break;
        }
    }

    public static void setOriginalDifficulty(double difficulty){
        originalDifficulty = difficulty;
    }

    public static void setEnteringName(boolean enteringNameValue){
        enteringName = enteringNameValue;
    }

    public static void stopMainMenuSong(){
        mainMenuSong.stop();
    }

    public static void startMainMenuSong(){
        mainMenuSong.play();
    }

    public static void stopGamePlaySongs(){
        gameplaySong.stop();
        level2Song.stop();
    }

    //Getters
    public static GraphicsContext getGc() {
        return gc;
    }

    public static Pane getRoot() {
        return gameplayElements;
    }

    public static GameObject getPlayer() {
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

    public static int getLevel() {
        return level;
    }

    public static int getCurrentScore() {
        return currentScore;
    }

    public static int getLivesLeft() {
        return livesLeft;
    }

    public static double getDifficulty() {return gameDifficulty; }

    public static double getOriginalDifficulty() {return originalDifficulty; }

    public static boolean getSavedScore() {return savedScore; }

    public static Rectangle getFloor() { return floor; }

    public static Rectangle getCeiling() { return ceiling; }

    public static boolean getGameOverState() { return gameOverState; }

    public static double getCurrentTime() {
        return currentTime;
    }

    public static double getLastLanderUpdate() {
        return lastLanderUpdate;
    }

    public static void setLastLanderUpdate(double lastLanderUpdate) {
        ApplicationStart.lastLanderUpdate = lastLanderUpdate;
    }

    public static int getEnemiesKillCount() {
        return enemiesKillCount;
    }

    public static void setEnemiesKillCount(int enemiesKillCount) {
        ApplicationStart.enemiesKillCount = enemiesKillCount;
    }

    public static void setCurrentScore(int currentScore) {
        ApplicationStart.currentScore = currentScore;
    }

    public static AudioClip getExplosionSound() {
        return explosionSound;
    }

    public static double getLastShot() {
        return lastShot;
    }
}