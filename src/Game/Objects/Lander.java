package Game.Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Lander extends GameObject{
    public Lander(double scale){
        super(50*scale,"enemy", new Rectangle(30*scale,40*scale, Color.INDIANRED));
        setVelocity(0,30);
    }
}
