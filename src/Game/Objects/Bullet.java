package Game.Objects;

import Game.Effects.Emitter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static Game.ApplicationStart.getEmitters;
import static Game.ApplicationStart.getPlayer;

public class Bullet extends GameObject{
    public Bullet(double scale, String type) {
        super(1000*scale, type, new Circle(6*scale, Color.BURLYWOOD));
        setVelocity(getPlayer().getForwardVector().multiply(1000).add(getPlayer().getVelocity()));
        getView()[0].setViewOrder(2);
        getEmitters().add(new Emitter(10000,1000,Color.LIGHTCYAN, 10, 0.5,"-velocity",Math.PI/50,0,0.3,-1,this));
    }
}
