package MainGame.Objects;

import MainGame.Layout.SceneSetup;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;

import java.io.File;

import static MainGame.Layout.SceneSetup.*;
import static MainGame.Layout.SceneSetup.updateRestartBtn;
import static MainGame.MainGame.*;
import static MainGame.Math.OwnMath.colorLerp;
import static MainGame.Math.OwnMath.getPlaceValue;
import static MainGame.Objects.Spawner.getL1EnemyCount;
import static MainGame.Objects.Spawner.resetSpawner;

public abstract class gameStateHandler {
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

    //File locations
    private static String highScoreFileName = "src/MainGame/highScores.txt";
    private static String mainMenuSongFile = "src/MainGame/AudioFiles/mainMenu.mp3";
    private static String shooterFile = "src/MainGame/AudioFiles/shooter.mp3";
    private static String deadFile = "src/MainGame/AudioFiles/dead.mp3";
    private static String screamFile = "src/MainGame/AudioFiles/scream.mp3";
    private static String explosionFile = "src/MainGame/AudioFiles/explosion.mp3";
    private static String gameplaySongFile = "src/MainGame/AudioFiles/cantina.mp3";
    private static String level2SongFile = "src/MainGame/AudioFiles/level2.mp3";

    //audio files
    private static AudioClip mainMenuSong = new AudioClip(new File(mainMenuSongFile).toURI().toString());
    private static AudioClip shooterSound = new AudioClip(new File(shooterFile).toURI().toString());
    private static AudioClip deadSound = new AudioClip(new File(deadFile).toURI().toString());
    private static AudioClip screamSound = new AudioClip(new File(screamFile).toURI().toString());
    private static AudioClip explosionSound = new AudioClip(new File(explosionFile).toURI().toString());
    private static AudioClip gameplaySong = new AudioClip(new File(gameplaySongFile).toURI().toString());
    private static AudioClip level2Song = new AudioClip(new File(level2SongFile).toURI().toString());

    public static void gameStateSetup() {
        gameplaySong.play();
        gameDifficulty = originalDifficulty;
        savedScore = false;
        livesLeft = 3;
        lastShot = 0;
        timeDilationLeft = 0;
        timeOfPenalty = 0;
        timeDilationLastUpdate = 0;
        timeOfLevel2 = -1;
        enteringName = false;
        enemiesKillCount = 0;
        level = 1;
        forceThrust = false;
        timeSpeed = 0.000_000_001;
        gameOverState = false;
        gameOverSince = -1;
        currentScore = 0;
        lastScoreUpdate = -10;
        lastLanderUpdate = -10;

        setLives(0);
        setTimeDilationFraction(0);
        resetSpawner();
        resetNameEnter();
    }

    public static void levelManagement(double time) {
        if (enemiesKillCount >= getL1EnemyCount()) {
            if (timeOfLevel2 == -1) {
                gameplaySong.stop();
                level2Song.play();
                timeOfLevel2 = time;
            }

            if (time - timeOfLevel2 > 5) {
                double difficultyChangeRate = 1.0 / (getOriginalDifficulty() * 60); //1 difficulty per 60 seconds if difficulty level set was 1
                gameDifficulty = getOriginalDifficulty() + (time - timeOfLevel2 - 5) * difficultyChangeRate;
            }

            int messageTime = 5;
            if (time - timeOfLevel2 < messageTime) {
                getRoot().getChildren().get(10).setVisible(true);
            } else {
                level = 2;
                getRoot().getChildren().get(10).setVisible(false);
                if (time - timeOfLevel2 < messageTime + 0.05) {
                    forceThrust = true;
                } else {
                    forceThrust = false;
                }
            }
        }
    }

    public static void playerAccelerate(double deltaTime) {
        //Accelerate player
        double playerAcceleration = getPlayer().getAcceleration() * deltaTime;

        // Level 2+
        if (getLevel() > 1) {
            getPlayer().setMaxVelocity(getScale() * getDifficulty() * 400);
            if (getForward() || forceThrust) {
                getPlayer().accelerate(getPlayer().getForwardVector().multiply(playerAcceleration));
            }
        }
        // Level 1
        else {
            getPlayer().setMaxVelocity(getScale() * getDifficulty() * 300);
            if (getLeft()) {
                getPlayer().accelerate(-playerAcceleration, 0);
            }
            if (getRight()) {
                getPlayer().accelerate(playerAcceleration, 0);
            }
            if (!(getLeft() || getRight())) {
                // if moving right faster than 15
                if (getPlayer().getVelocity().getX() > 15) {
                    getPlayer().accelerate(-playerAcceleration, 0);
                }
                // if moving left faster than 15
                else if (getPlayer().getVelocity().getX() < -15) {
                    getPlayer().accelerate(playerAcceleration, 0);
                }
                //if moving close to zero
                else {
                    getPlayer().setVelocity(0, 0);
                }
            }
        }
    }

    public static void gameOverPass(double time) {
        //store the earliest time that gameover was detected
        if (gameOverSince == -1) {
            gameOverSince = time;
            deadSound.play();
        }
        getRoot().getChildren().get(8).setVisible(false);
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
                getTimer().stop();
            } else {
                updateRestartBtn("Restart (" + (int) (secBeforeRestart - secSinceGameOver + 1) + ")");
            }
        } else {
            updateRestartBtn("Restart");
        }
    }

    public static void timeDilationPass(double time) {
        //speed is relative to 1
        double increaseSpeed = 0.2;
        double decreaseSpeed = 1;

        //time dilation timer
        //if still in penalty don't run
        if (time - timeOfPenalty < penaltyTime) {
            setTimeDilated(false);
            timeDilationLeft += increaseSpeed * (time - timeDilationLastUpdate);
            setTimeDilationColor(Color.INDIANRED);
        } else {
            setTimeDilationColor(Color.LIGHTGREEN);
            if (getSpace()) {
                setTimeDilated(true);
                timeDilationLeft -= decreaseSpeed * (time - timeDilationLastUpdate);
            } else {
                setTimeDilated(false);
                if (timeDilationLeft + time - timeDilationLastUpdate > timeDilationMax) {
                    timeDilationLeft = timeDilationMax;
                } else {
                    timeDilationLeft += increaseSpeed * (time - timeDilationLastUpdate);
                }
            }
            if (timeDilationLeft < 0.01) {
                timeOfPenalty = time;
            }
        }
        setTimeDilationFraction(timeDilationLeft / timeDilationMax);
        timeDilationLastUpdate = time;
    }

    public static void shootPass(double time) {
        if (getShoot() && time - lastShot > Player.getRechargeTime() && !getGameOverState()) {
            shooterSound.play();
            Spawner.addGameObject(new Bullet(getScale(), "bullet"), getPlayer().getView()[0].getTranslateX(), getPlayer().getView()[0].getTranslateY());
            lastShot = time;
        }
    }

    public static void showScoreUpdate(double time) {
        double timeUpdateTime = 0.3;
        if ((time - lastScoreUpdate) / timeUpdateTime > 1) {
            SceneSetup.updateScoreColour(Color.WHITE);
        } else {
            SceneSetup.updateScoreColour(colorLerp(Color.DARKBLUE, Color.LIGHTGREEN, (time - lastScoreUpdate) / timeUpdateTime));
        }
    }

    public static void updateHUDandScore(double time) {
        //Update HUD
        int min = (int) time / 60;
        int sec = (int) (time - (int) (time / 60) * 60);
        String timeString = Integer.toString(getPlaceValue(min, 10)) + getPlaceValue(min, 1) + ":" + getPlaceValue(sec, 10) + getPlaceValue(sec, 1);
        SceneSetup.updateTime(timeString);

        //Update score
        SceneSetup.updateScore("Score: " + getCurrentScore());
    }

    public static void updateLives() {
        setLives(livesLeft);
    }

    public static void playMainMenuSong() {
        mainMenuSong.play();
    }

    public static double getTimeSpeed() {
        return timeSpeed;
    }

    public static boolean getForceThrust() {
        return forceThrust;
    }

    public static void lostLife() {
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

    private static void setTimeDilated(boolean timeDilated) {
        if (timeDilated) {
            timeSpeed = 0.000_000_0003;
        } else {
            timeSpeed = 0.000_000_001;
        }
        getRoot().getChildren().get(8).setVisible(timeDilated);
    }

    public static void setGameOverVisible(boolean visible) {
        //set gameOverMenu to visible value
        getRoot().getChildren().get(9).setVisible(visible);
        //set gameOver Darken Rectangle to visible value
        getRoot().getChildren().get(7).setVisible(visible);
    }

    public static void setSavedScore(boolean saved) {
        savedScore = saved;
    }

    private static void setLives(int livesLeft) {
        switch (livesLeft) {
            case 3:
                getRoot().getChildren().get(4).setVisible(true);
                getRoot().getChildren().get(5).setVisible(true);
                getRoot().getChildren().get(6).setVisible(true);
                break;
            case 2:
                getRoot().getChildren().get(4).setVisible(true);
                getRoot().getChildren().get(5).setVisible(true);
                getRoot().getChildren().get(6).setVisible(false);
                break;
            case 1:
                getRoot().getChildren().get(4).setVisible(true);
                getRoot().getChildren().get(5).setVisible(false);
                getRoot().getChildren().get(6).setVisible(false);
                break;
            case 0:
                getRoot().getChildren().get(4).setVisible(false);
                getRoot().getChildren().get(5).setVisible(false);
                getRoot().getChildren().get(6).setVisible(false);
                break;
        }
    }

    public static void setOriginalDifficulty(double difficulty) {
        originalDifficulty = difficulty;
    }

    public static void setEnteringName(boolean enteringNameValue) {
        enteringName = enteringNameValue;
    }

    public static void stopMainMenuSong() {
        mainMenuSong.stop();
    }

    public static void startMainMenuSong() {
        mainMenuSong.play();
    }

    public static void stopGamePlaySongs() {
        gameplaySong.stop();
        level2Song.stop();
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

    public static double getDifficulty() {
        return gameDifficulty;
    }

    public static double getOriginalDifficulty() {
        return originalDifficulty;
    }

    public static boolean getSavedScore() {
        return savedScore;
    }

    public static boolean getGameOverState() {
        return gameOverState;
    }

    public static double getLastLanderUpdate() {
        return lastLanderUpdate;
    }

    public static void setLastLanderUpdate(double lastLanderUpdate) {
        gameStateHandler.lastLanderUpdate = lastLanderUpdate;
    }

    public static int getEnemiesKillCount() {
        return enemiesKillCount;
    }

    public static void setEnemiesKillCount(int enemiesKillCount) {
        gameStateHandler.enemiesKillCount = enemiesKillCount;
    }

    public static void setCurrentScore(int currentScore) {
        gameStateHandler.currentScore = currentScore;
    }

    public static AudioClip getExplosionSound() {
        return explosionSound;
    }

    public static double getLastShot() {
        return lastShot;
    }

    public static String getHighScoreFileName() {
        return highScoreFileName;
    }
}
