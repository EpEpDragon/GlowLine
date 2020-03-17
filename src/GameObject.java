import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

import javax.lang.model.type.NullType;

public class GameObject {

    //WHY THE FUCK DOES THE NODE'S ROTATE FUNCTION USE DEGREES?????!
    final private double radToDegConst = 180/Math.PI;
    final private double degToRadConst = Math.PI/180;

    private Node view;
    private Point2D velocity;
    private double maxVelocity = 300;

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

    public void setVelocity(Point2D velocity){
        if(velocity.distance(0,0) <= maxVelocity) {
            this.velocity = velocity;
        }else{
            this.velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

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

//    public boolean isColliding(GameObject object){
//        if (Shape.intersect((Shape)view,(Shape)object.view) != null){
//            return true;
//        }
//        return false;
//    }

    public boolean isColliding(Shape shape){
        if (Shape.intersect((Shape)view,shape).getBoundsInLocal().getWidth() != -1){
            return true;
        }
        return false;
    }
}
