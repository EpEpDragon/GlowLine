package MainGame.Objects;

import MainGame.Effects.Emitter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static MainGame.MainGame.*;
import static MainGame.Math.OwnMath.colorLerp;
import static MainGame.Objects.Spawner.removeGameObject;
import static MainGame.Objects.Spawner.removeGameObjectAll;
import static MainGame.Objects.gameStateHandler.*;

public class Lander extends GameObject {
    private static boolean lighter;

    public Lander(double scale) {
        super(80 * scale * getDifficulty(), "enemy", new Rectangle(30 * scale, 40 * scale, Color.INDIANRED));
        setVelocity(0, 80 * scale * getDifficulty());
        lighter = false;
    }

    @Override
    void handelCollisions(double deltaTime) {
        Collision collision;

        //lander, floor collision; lose life
        collision = this.getCollision(getFloor());
        landerCollision(collision);
        //lander, bullet collision; kill lander
        for (GameObject bullet : getBullets()) {
            collision = getCollision(bullet);
            if (collision.isCollided() && !getGameOverState()) {
                setEnemiesKillCount(getEnemiesKillCount() + 1);
                removeGameObjectAll(this, bullet);
                setCurrentScore(getCurrentScore() + 20);
                setLastLanderUpdate(getCurrentTime());
                getExplosionSound().play();
            }
        }
        //lander, player collision; lose life
        collision = getCollision(getPlayer());
        landerCollision(collision);
    }

    private void landerCollision(Collision collision) {
        //if lander (collided with floor) or (collided with player)
        if (collision.isCollided()) {
            //if on last life and gameOver ensues
            if (getLivesLeft() == 0) {
                setVelocity(0, 0);
                if (!getGameOverState()) {
                    gameOver();
                    getEmitters().add(new Emitter(20000, 2000, Color.RED, Color.LIGHTGREEN, 10, 1, "0", Math.PI * 2, 0, 0.3, 0.1, this));
                }

                // Show lander hit floor with colour animation
                double landerUpdateTime = 0.03;
                if ((getCurrentTime() - getLastLanderUpdate()) / landerUpdateTime > 1) {
                    setLastLanderUpdate(getCurrentTime());
                    lighter = !lighter;
                }
                if (lighter) {
                    setRectColour(colorLerp(Color.INDIANRED, Color.ORANGERED, (getCurrentTime() - getLastLanderUpdate()) / landerUpdateTime));
                } else {
                    setRectColour(colorLerp(Color.ORANGERED, Color.INDIANRED, (getCurrentTime() - getLastLanderUpdate()) / landerUpdateTime));
                }
            } else {
                //if not on last life, just lose a life
                lostLife();
                getEmitters().add(new Emitter(20000, 2000, Color.RED, Color.LIGHTGREEN, 10, 1, "0", Math.PI * 2, 0, 0.3, 0.1, this));
                removeGameObject(this);
            }
        }
    }
}
