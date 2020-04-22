package Game.Objects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class LanderLevel1 extends GameObject{
    public LanderLevel1(double scale){
        super(10*scale,"enemy", new Rectangle(50*scale,50*scale, Color.INDIANRED));
        setVelocity(0,10);
        for(Node view: getView()){
            view.setViewOrder(2);
        }
    }
}