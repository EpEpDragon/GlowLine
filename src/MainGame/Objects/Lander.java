package MainGame.Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static MainGame.ApplicationStart.*;
import static MainGame.Math.OwnMath.colorLerp;
import static MainGame.Objects.Spawner.removeGameObject;
import static MainGame.Objects.Spawner.removeGameObjectAll;

public class Lander extends GameObject{
    private static boolean lighter;
    public Lander(double scale){
        super(80*scale*getDifficulty(),"enemy", new Rectangle(30*scale,40*scale, Color.INDIANRED));
        setVelocity(0,80*scale*getDifficulty());
        lighter = false;
    }

    @Override
    void handelCollisions(double deltaTime) {
        //Lander collision
        CollisionHandler.Collision collision;

        //floor
        collision = this.getCollision(getFloor());
        landerCollision(collision);
        //bullet
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
        collision = getCollision(getPlayer());
        landerCollision(collision);
    }

    private void landerCollision(CollisionHandler.Collision collision) {
        if (collision.isCollided()) {
            if (getLivesLeft() == 0) {
                setVelocity(0, 0);
                if (!getGameOverState()) {
                    gameOver();
                }
                // Show lander hit floor.
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
            }
            else {
                lostLife();
                removeGameObject(this);
            }
        }
    }
}
