package MainGame.Objects;

import MainGame.Effects.Emitter;
import MainGame.Math.OwnMath;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static MainGame.MainGame.*;
import static MainGame.Objects.Spawner.removeGameObjectAll;
import static MainGame.Objects.gameStateHandler.*;

public class Kamikaze extends GameObject {
    private int acceleration = 1300;

    public Kamikaze(double scale) {
        super(900 * scale * getDifficulty(), Color.WHITE, Color.BLACK, "kamikaze", new Circle(18 * scale, Color.TRANSPARENT),
                new Circle(20 * scale, Color.TRANSPARENT),
                new Circle(15 * scale, Color.TRANSPARENT),
                new Circle(10 * scale, Color.TRANSPARENT));
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        //Accelerate to player
        Point2D interceptVec = OwnMath.findInterceptVector(new Point2D(getView()[0].getTranslateX(), getView()[0].getTranslateY()), new Point2D(getPlayer().getView()[0].getTranslateX(), getPlayer().getView()[0].getTranslateY()), getVelocity(), getPlayer().getVelocity(), getMaxVelocity()).normalize();
        if (interceptVec.getX() > (float)getResolutionX()/4){
            interceptVec = new Point2D(-interceptVec.getX(), interceptVec.getY());
        }
        accelerate(interceptVec.multiply(acceleration * deltaTime));
    }

    @Override
    public void setDead() {
        super.setDead();
        getEmitters().add(new Emitter(40000, 2000, Color.hsb(10, 0.6, 0.7), Color.hsb(180, 0.9, 0.5), 10, 1, "0", Math.PI * 2, 0, 0.3, 0.1, this));
    }

    @Override
    void handelCollisions(double deltaTime) {
        CollisionHandler.Collision collision;
        for (GameObject allyBullet : getBullets()) {
            collision = this.getCollision(allyBullet);
            if (collision.isCollided() && !getGameOverState()) {
                removeGameObjectAll(this, allyBullet);
                setCurrentScore(getCurrentScore() + 100);
                setLastLanderUpdate(getCurrentTime());
                getExplosionSound().play();
            }
        }

        //Floor collision
        collision = this.getCollision(getFloor());
        if (collision.isCollided()) {
            double deltaY = collision.getY() - getFloor().getY();
            setVelocity(getVelocity().getX(), 0);
            getCollisionShape().setTranslateY(getCollisionShape().getTranslateY() - deltaY);
        }
    }
}
