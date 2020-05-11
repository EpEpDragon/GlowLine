package MainGame.Objects;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static MainGame.ApplicationStart.getDifficulty;

public class Lander extends GameObject{
    public Lander(double scale){
        super(80*scale*getDifficulty(),"enemy", new Rectangle(30*scale,40*scale, Color.INDIANRED));
        setVelocity(0,80*scale*getDifficulty());
//        for(Node view: getView()){
//            view.setViewOrder(2);
//        }
    }
}
