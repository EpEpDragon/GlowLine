package MainGame.Effects;

import MainGame.MainGame;
import MainGame.Math.OwnMath;
import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    private double deltaTime;
    private GraphicsContext gc;

    private double x;
    private double y;
    private int radius;
    private Color colorS;
    private Color colorE;
    private Point2D velocity;
    private double life = 1;
    private double decay;

    Particle(double x, double y, int radius, Color colorS, Color colorE, Point2D velocity, double decay, double deltaTime) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.colorS = colorS;
        this.colorE = colorE;
        this.velocity = velocity;
        this.decay = decay;
        this.deltaTime = deltaTime;

        gc = MainGame.getGc();
    }

    public void render() {
        gc.setFill(OwnMath.colorLerp(colorS, colorE, OwnMath.clamp(1 - life, 0, 1)));
        gc.setGlobalAlpha(life);
        gc.fillOval(x, y, radius, radius);
    }

    public void update() {
        x += velocity.getX() * deltaTime;
        y += velocity.getY() * deltaTime;

        //Bounce off floor
        if (y >= MainGame.getResolutionY() - 20 - radius) {
            y = MainGame.getResolutionY() - 20 - radius;
            velocity = new Point2D(velocity.getX(), velocity.getY() * -Math.abs(Math.sin(velocity.getX() / velocity.distance(0, 0))));
        }

        life -= decay * deltaTime;
    }

    public boolean isDead() {
        if (life <= 0) {
            return true;
        } else {
            return false;
        }
    }
}
