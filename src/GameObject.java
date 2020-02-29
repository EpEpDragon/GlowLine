import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;



public class GameObject {
    private Node view;
    private Point2D velocity;
    private double maxVelocity = 300;

    GameObject (Node view, Color color){
        this.view = view;
        setVelocity(new Point2D(0,0));

        if (view instanceof javafx.scene.shape.Polygon){
            ((Polygon) view).setStroke(color);
        }
    }

    public Node getView(){
        return view;
    }

    public void setRotation(double angle){
        view.setRotate(angle);
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

    public double getRotation(){
        return getRotation();
    }
}
