package MainGame.Objects;

import MainGame.Math.OwnMath;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static MainGame.MainGame.*;
import static MainGame.Objects.gameStateHandler.getLevel;

public abstract class Spawner {
    //Lander spawn time
    static final int landerSpawnTime = 2;
    static double previousTimeLander = 0; //if changed, change in resetSpawner as well

    //LanderLevel1 spawn time
    static final int landerL1SpawnTime = 2;
    static final int landerL1Rows = 2;
    static final int landerL1Columns = 3;
    static int landerL1Done = 0; //if changed, change in resetSpawner as well
    static double previousTimeLanderL1 = -landerL1SpawnTime;//if changed, change in resetSpawner as well

    //Kamikaze spawn time
    static final int kamikazeSpawnTime = 5;
    static double previousTimeKamikaze = 0;//if changed, change in resetSpawner as well

    public static void spawnPass(double time) {
        //Level 2+
        if (getLevel() > 1) {
            //Lander spawn logic
            if (time - previousTimeLander >= landerSpawnTime) {
                addGameObject(new Lander(getScale()), OwnMath.clamp(getResolutionX() * Math.random(), getResolutionX() * 0.1, getResolutionX() * 0.9), getResolutionY() * -0.1);
                previousTimeLander = time;
            }

            //Kamikaze spawn logic
            if (time - previousTimeKamikaze >= kamikazeSpawnTime && time > 5) {
                addGameObject(new Kamikaze(getScale()), OwnMath.clamp(getResolutionX() * Math.random(), getResolutionX() * 0.1, getResolutionX() * 0.9), getResolutionY() * -0.1);
                previousTimeKamikaze = time;
            }
        }
        // level 1
        else {
            if (landerL1Done < landerL1Rows && time - previousTimeLanderL1 >= landerL1SpawnTime) {
                for (int i = 1; i <= landerL1Columns; i++) {
                    addGameObject(new Lander(getScale()), (getResolutionX() / (float) (landerL1Columns + 1)) * i - 50 * getScale() / 2, getResolutionY() * -0.05);
                }
                previousTimeLanderL1 = time;
                landerL1Done++;
            }
        }
    }

    public static void addGameObject(GameObject object, double x, double y) {
        spawnGameObject(object, x, y);
        switch (object.getType()) {
            case "bullet":
                getBullets().add(object);
                break;
            case "enemyBullet":
            case "kamikaze":
                getEnemyBullets().add(object);
                break;
            case "enemy":
                getEnemies().add(object);
                break;
        }
    }

    public static void spawnGameObject(GameObject object, double x, double y) {
        for (Node view : object.getView()) {
            view.setTranslateX(x);
            view.setTranslateY(y);

            getGameplayElements().getChildren().add(view);
        }
        object.getCollisionShape().setTranslateX(x);
        object.getCollisionShape().setTranslateY(y);
    }

    public static void removeGameObject(GameObject object) {
        object.setDead();
        for (Node view : object.getView()) {
            getGameplayElements().getChildren().remove(view);
        }
    }

    public static void removeGameObjectAll(GameObject... objects) {
        for (GameObject object : objects) {
            object.setDead();

            for (Node view : object.getView()) {
                getGameplayElements().getChildren().remove(view);
            }
        }
    }

    public static int getL1EnemyCount() {
        return landerL1Columns * landerL1Rows;
    }

    public static void resetSpawner() {
        landerL1Done = 0;
        previousTimeLanderL1 = -landerL1SpawnTime;
        previousTimeKamikaze = 0;
        previousTimeLander = 0;
    }

    public static void spawnBackground() {
        //Background (atmosphere)
        Node tempBackground;
        tempBackground = new Circle(0.5 * getResolutionX(), 4.2 * getResolutionY(), 4 * getResolutionY(),
                Color.color(1, 1, 1, 0.1));
        tempBackground.setMouseTransparent(true);
        //tempBackground.setViewOrder(4);
        getGameplayElements().getChildren().add(tempBackground);

        tempBackground = new Circle(0.5 * getResolutionX(), 4.5 * getResolutionY(), 4 * getResolutionY(),
                Color.color(1, 1, 1, 0.15));
        tempBackground.setMouseTransparent(true);
        //tempBackground.setViewOrder(4);
        getGameplayElements().getChildren().add(tempBackground);

        tempBackground = new Circle(0.5 * getResolutionX(), 4.8 * getResolutionY(), 4 * getResolutionY(),
                Color.color(1, 1, 1, 0.135));
        tempBackground.setMouseTransparent(true);
        //tempBackground.setViewOrder(4);
        getGameplayElements().getChildren().add(tempBackground);
    }
}
