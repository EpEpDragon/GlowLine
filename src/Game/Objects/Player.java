package Game.Objects;

import Game.Math.OwnMath;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static Game.ApplicationStart.*;

public class Player extends GameObject{

    public Player(double scale) {
        super(500, Color.WHEAT, Color.BLACK, "ally", new Polygon(20*scale, 0*scale, -18*scale, 18*scale, -18*scale, -18*scale));
//        for(Node view: getView()){
//            view.setViewOrder(1);
//        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        for(Node view : getView()) {
            //Level 1 turret must not be able to aim below horizontal
            if (getLevel()>1) {
                setRotation(OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true));
            }
            else {
                double newAngle = OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true);
                if (newAngle<0) {
                    setRotation(newAngle);
                }
                else {
                    if (newAngle<(Math.PI/2) && newAngle>(-Math.PI/2)) {
                        setRotation(0);
                    }
                    else{
                        setRotation(Math.PI);
                    }
                }
            }

            //Teleport player to other side of screen if off-screen
            if (view.getTranslateX() < getResolutionX() * -0.01) {
                view.setTranslateX(getResolutionX() + getResolutionX() * 0.01);
            } else if (view.getTranslateX() > getResolutionX() + getResolutionX() * 0.01) {
                view.setTranslateX(getResolutionX() * -0.01);
            }
        }
    }

    public int getAcceleration() {
        //px/s
        return 800;
    }
}
