package Game.Objects;

import Game.Effects.Emitter;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static Game.ApplicationStart.getEmitters;
import static Game.ApplicationStart.getPlayer;

public class Bullet extends GameObject{
    public Bullet(double scale, String type) {
        super(1000*scale, type, new Circle(6*scale, Color.BURLYWOOD));
        setVelocity(getPlayer().getForwardVector().multiply(800).add(getPlayer().getVelocity()));
        getView()[0].setViewOrder(2);
        getEmitters().add(new Emitter(10000,100,Color.LIGHTCYAN, 1,Math.PI/10,0,0.3,-1,this));
    }
}
