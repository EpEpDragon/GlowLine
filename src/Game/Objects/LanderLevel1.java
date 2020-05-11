package Game.Objects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static Game.ApplicationStart.getDifficulty;

public class LanderLevel1 extends GameObject{
    public LanderLevel1(double scale){
        super(80*scale*getDifficulty(),"enemy", new Rectangle(50*scale,50*scale, Color.INDIANRED));
        setVelocity(0,80*scale*getDifficulty());
//        for(Node view: getView()){
//            view.setViewOrder(2);
//        }
    }
}