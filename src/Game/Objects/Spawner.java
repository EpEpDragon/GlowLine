package Game.Objects;

import Game.ApplicationStart;
import Game.Math.OwnMath;
import javafx.scene.Node;


public abstract class Spawner extends ApplicationStart {
    //Lander spawn time
    static final int landerSpawnTime = 2;
    static double previousTimeLander = landerSpawnTime;

    //LanderLevel1 spawn time
    static final int landerL1SpawnTime = 4;
    static final int landerL1Rows = 3; //if changed, update level upgrade kill count
    static int landerL1Done = 0;
    static double previousTimeLanderL1 = landerL1SpawnTime;

    //Kamikaze spawn time
    static final int kamikazeSpawnTime = 3;
    static double previousTimeKamikaze = kamikazeSpawnTime;

    public static void spawnPass(double time){
        //Level 2+
        if (getLevel()>1) {
            //Lander spawn logic
            if (time - previousTimeLander >= landerSpawnTime) {
                addGameObject(new Lander(getScale()), OwnMath.clamp(resolutionX * Math.random(), resolutionX * 0.1, resolutionX * 0.9), resolutionY * -0.1);
                previousTimeLander = time;
            }

            //Kamikaze spawn logic
            if (time - previousTimeKamikaze >= kamikazeSpawnTime && time > 5) {
                addGameObject(new Kamikaze(getScale()), OwnMath.clamp(resolutionX * Math.random(), resolutionX * 0.1, resolutionX * 0.9), resolutionY * -0.1);
                previousTimeKamikaze = time;
            }
        }
        // level 1
        else{
            if (landerL1Done<landerL1Rows && time - previousTimeLanderL1 >= landerL1SpawnTime) {
                for (int i = 1; i < 8; i++) { // if number of loops changed, update level upgrade kill count
                    addGameObject(new LanderLevel1(getScale()), resolutionX * 0.1 * i, resolutionY * -0.1);
                    previousTimeLanderL1 = time;
                }
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
        for(Node view : object.getView()) {
            view.setTranslateX(x);
            view.setTranslateY(y);

            getRoot().getChildren().add(view);
        }
        object.getCollisionShape().setTranslateX(x);
        object.getCollisionShape().setTranslateY(y);
    }
}
