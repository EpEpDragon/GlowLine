package Game.Objects;

import Game.Effects.Emitter;
import Game.Math.OwnMath;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static Game.ApplicationStart.getEmitters;
import static Game.ApplicationStart.getPlayer;

public class Kamikaze extends GameObject{
    private int acceleration = 900;
    public Kamikaze(double scale){
        super(900*scale, Color.WHITE, Color.BLACK, "kamikaze", new Circle(18*scale, Color.TRANSPARENT),
                new Circle(20*scale, Color.TRANSPARENT),
                new Circle(15*scale, Color.TRANSPARENT),
                new Circle(10*scale, Color.TRANSPARENT));
        for(Node view: getView()){
            view.setViewOrder(2);
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        //Accelerate to player
        Point2D interceptVec = OwnMath.findInterceptVector(new Point2D(getView()[0].getTranslateX(), getView()[0].getTranslateY()), new Point2D(getPlayer().getView()[0].getTranslateX(), getPlayer().getView()[0].getTranslateY()), getVelocity(), getPlayer().getVelocity(), getMaxVelocity()).normalize();
        accelerate(interceptVec.multiply(acceleration*deltaTime));
    }

    @Override
    public void setDead() {
        super.setDead();
        getEmitters().add(new Emitter(40000, 2000, Color.hsb(10, 0.6,0.7), Color.hsb(180, 0.9,0.5), 10,1,"0", Math.PI*2,0,0.3, 0.1, this));
    }
}
