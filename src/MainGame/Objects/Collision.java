package MainGame.Objects;

public class Collision {
    double x;
    double y;
    boolean collided;

    public Collision(boolean collided, double x, double y) {
        this.x = x;
        this.y = y;
        this.collided = collided;
    }

    public boolean isCollided() {
        return collided;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}
