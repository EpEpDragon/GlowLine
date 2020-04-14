package Game.Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static Game.ApplicationStart.getPlayer;

public class Bullet extends GameObject{
    public Bullet(double scale, String type) {
        super(1000*scale, type, new Circle(6*scale, Color.BURLYWOOD));
        setVelocity(getPlayer().getForwardVector().multiply(800).add(getPlayer().getVelocity()));
    }
}
