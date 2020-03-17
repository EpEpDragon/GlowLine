import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import javax.lang.model.type.NullType;
import java.security.cert.PolicyNode;
import java.util.HashMap;
import java.util.Map;

public class GameObject {

    //WHY THE FUCK DOES THE NODE'S ROTATE FUNCTION USE DEGREES?????!
    final private double radToDegConst = 180/Math.PI;
    final private double degToRadConst = Math.PI/180;

    private Node view;
    private Point2D velocity;
    private double maxVelocity = 300;
    private boolean alive = true;

    GameObject (Node view, double maxVelocity, Color color){
        this.view = view;
        setVelocity(new Point2D(0,0));
        this.maxVelocity = maxVelocity;
        if (view instanceof javafx.scene.shape.Polygon){
            ((Polygon) view).setStroke(color);
        }
    }

    GameObject (Node view, double maxVelocity){
        this.view = view;
        this.maxVelocity = maxVelocity;
        setVelocity(new Point2D(0,0));
    }

    public Node getView(){
        return view;
    }

    public void setRotation(double angle){
        view.setRotate(angle*radToDegConst);
    }

    public void setAlive(boolean alive) { this.alive = alive; }
    public boolean isAlive(){ return alive; }
    public void setVelocity(Point2D velocity){
        if(velocity.distance(0,0) <= maxVelocity) {
            this.velocity = velocity;
        }else{
            this.velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public void setVelocity(double x, double y){
        Point2D velocity = new Point2D(x,y);
        if(x*x + y*y <= maxVelocity*maxVelocity) {
            this.velocity = velocity;
        }else{
            this.velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public double getMaxVelocity() { return maxVelocity; }
    public Point2D getVelocity(){
        return velocity;
    }

    public Point2D getForwardVector(){
        return new Point2D(Math.cos(view.getRotate() * degToRadConst), Math.sin(view.getRotate() * degToRadConst));
    }
//    public Point2D getSidewaysVector(){
//        return new Point2D(Math.cos(view.getRotate() * degToRadConst ), Math.sin(view.getRotate() * degToRadConst * -1));
//    }

    public double getRotation(){
        return view.getRotate() * degToRadConst;
    }

    public Collision isColliding(GameObject object){
        Shape resultShape = Shape.intersect((Shape)view, (Shape)object.getView());

        Collision collision = new Collision(!resultShape.getBoundsInLocal().isEmpty(), resultShape.getBoundsInLocal().getMaxX(),
                resultShape.getBoundsInLocal().getMaxY());

        return collision;
    }

    public Collision isColliding(Shape shape){
        Shape resultShape = Shape.intersect((Shape)view,shape);

        Collision collision = new Collision(!resultShape.getBoundsInLocal().isEmpty(), resultShape.getBoundsInLocal().getMaxX(),
                resultShape.getBoundsInLocal().getMaxY());

        return collision;
    }

    public class Collision{
        double x;
        double y;
        boolean collided;
        Collision(boolean collided, double x, double y){
            this.x = x;
            this.y = y;
            this.collided = collided;
        }

    }
}
