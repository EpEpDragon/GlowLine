package MainGame.Objects;

import MainGame.Effects.Emitter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static MainGame.ApplicationStart.getEmitters;
import static MainGame.ApplicationStart.getPlayer;

public class Bullet extends GameObject{
    public Bullet(double scale, String type) {
        super(1000*scale, type, new Circle(10*scale, Color.ORANGERED));
        setVelocity(getPlayer().getForwardVector().multiply(1000).add(getPlayer().getVelocity()));
        //transparent border enlarges effective bullet area to reduce chance of missing frame where collision occurs
        setCircleBorder();
//        getView()[0].setViewOrder(2);
        getEmitters().add(new Emitter(10000,1000, Color.hsb(30, 0.5,0.8), Color.hsb(27, 0.2,0.7), 10, 0.5,"-velocity",Math.PI/50,0,0.3,-1,this));
    }
}
