package Game.Objects;

import Game.Math.OwnMath;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import static Game.ApplicationStart.*;

public class Player extends GameObject{
    //px/s
    private int acceleration = 800;

    public Player(double scale) {
        super(500, Color.WHEAT, Color.BLACK, "ally", new Polygon(20*scale, 0*scale, -18*scale, 18*scale, -18*scale, -18*scale));
        for(Node view: getView()){
            view.setViewOrder(1);
        }
    }

    @Override
    public void update(double deltaTime) {
        super.update(deltaTime);
        for(Node view : getView()) {
            setRotation(OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true));

            //Teleport player to other side of screen if off-screen
            if (view.getTranslateX() < getResolutionX() * -0.01) {
                view.setTranslateX(getResolutionX() + getResolutionX() * 0.01);
            } else if (view.getTranslateX() > getResolutionX() + getResolutionX() * 0.01) {
                view.setTranslateX(getResolutionX() * -0.01);
            }
        }
    }

    public int getAcceleration() {
        return acceleration;
    }
}
