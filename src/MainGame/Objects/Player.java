package MainGame.Objects;

import MainGame.Effects.Emitter;
import MainGame.Math.OwnMath;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static MainGame.MainGame.*;
import static MainGame.Math.OwnMath.colorLerp;
import static MainGame.Objects.gameStateHandler.*;

public class Player extends GameObject {
    private static double rechargeTime = 0.5;

    public Player(double scale) {
        super(500, Color.WHEAT, Color.BLACK, "ally", new Polygon(20 * scale, 0 * scale, -18 * scale, 18 * scale, -18 * scale, -18 * scale));
//        for(Node view: getView()){
//            view.setViewOrder(1);
//        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        for (Node view : getView()) {
            //Level 1 turret must not be able to aim below horizontal
            if (getLevel() > 1) {
                setRotation(OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true));
            } else {
                double newAngle = OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true);
                if (newAngle < 0) {
                    setRotation(newAngle);
                } else {
                    if (newAngle < (Math.PI / 2) && newAngle > (-Math.PI / 2)) {
                        setRotation(0);
                    } else {
                        setRotation(Math.PI);
                    }
                }
            }

            // Show recharge
            if ((getCurrentTime() - getLastShot()) / rechargeTime > 1) {
                setPolygonFillColour(Color.WHEAT);
            } else {
                setPolygonFillColour(colorLerp(Color.WHEAT, Color.BLACK, (getCurrentTime() - getLastShot()) / rechargeTime));
            }
        }
    }

    @Override
    void handelCollisions(double deltaTime) {
        CollisionHandler.Collision collision;
        double deltaY;
        //player, floor collision; keep above floor
        collision = getCollision(getFloor());
        if (collision.isCollided()) {
            deltaY = collision.getY() - getFloor().getY();
            setVelocity(getVelocity().getX(), 0);
            getView()[0].setTranslateY(getView()[0].getTranslateY() - deltaY);
        }

        //player, ceiling collision; bounce
        collision = getCollision(getCeiling());
        if (collision.isCollided()) {
            deltaY = collision.getY() - getCeiling().getHeight();
            setVelocity(getVelocity().getX(), getVelocity().getY() - deltaY / deltaTime);
        }

        //player, enemyBullet collision; lose life
        for (GameObject enemyBullet : getEnemyBullets()) {
            collision = getCollision(enemyBullet);
            if (collision.isCollided()) {
                if (getLivesLeft() == 0) {
                    Spawner.removeGameObjectAll(enemyBullet, this);
                    gameOver();
                } else {
                    lostLife();
                    Spawner.removeGameObject(enemyBullet);
                }
            }
        }
    }

    public int getAcceleration() {
        //px.s
        return 800;
    }

    public static double getRechargeTime() {
        return rechargeTime;
    }
}
