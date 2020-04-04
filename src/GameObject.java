import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Shape;

public class GameObject{

    //WHY THE FUCK DOES THE NODE'S ROTATE FUNCTION USE DEGREES?????!
    final private double radToDegConst = 180/Math.PI;
    final private double degToRadConst = Math.PI/180;

    private Node[] view;
    private Shape collisionShape;
    private Point2D velocity = new Point2D(0,0);
    private double maxVelocity;
    private boolean dead = false;

    /************************************************************
     ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼Constructors▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
    ************************************************************/

    //For one shape, stroke color independent
    GameObject (double maxVelocity, Color color, Node view){
        this.view = new Node[1];
        this.view[0] = view;
        this.collisionShape = (Shape)view;

        //if polygon
        if (view instanceof javafx.scene.shape.Polygon){
            ((Polygon)view).setStroke(color);
        }
        //if circle
        if (view instanceof javafx.scene.shape.Circle){
            ((Circle)view).setStroke(color);
        }

        this.maxVelocity = maxVelocity;
    }

    //For one shape
    GameObject (double maxVelocity, Node view){
        this.view = new Node[1];
        this.view[0] = view;
        this.collisionShape = (Shape)view;
        this.maxVelocity = maxVelocity;
    }

    //For multiple shapes, stroke color independent
    GameObject (double maxVelocity, Color color, Node collisionShape, Node... views){
        this.view = new Node[views.length];
        for (int i = 0; i < views.length; i++) {
            this.view[i] = views[i];

            //if polygon
            if (view[i] instanceof javafx.scene.shape.Polygon){
                ((Polygon)view[i]).setStroke(color);
            }
            //if circle
            if (view[i] instanceof javafx.scene.shape.Circle){
                ((Circle)view[i]).setStroke(color);
            }
        }
        this.collisionShape = (Shape)collisionShape;
        this.maxVelocity = maxVelocity;
    }

    //For multiple shapes
    GameObject (double maxVelocity, Node collisionShape, Node... views){
        this.view = new Node[views.length];
        for (int i = 0; i < views.length; i++) {
            this.view[i] = views[i];
        }
        this.collisionShape = (Shape)collisionShape;
        this.maxVelocity = maxVelocity;
    }

    /************************************************************
     ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲Constructors▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
     ************************************************************/

    public Node[] getView(){
        return view;
    }

    public Shape getCollisionShape(){
        return (Shape)collisionShape;
    }
    public void setRotation(double angle){
        for (Node view : this.view) {
            view.setRotate(angle*radToDegConst);
        }
    }

    public void setDead(boolean alive) { this.dead = true; }
    public boolean isDead(){ return dead; }

    public void update(double deltaTime){
        for(Node view : this.view) {
            view.setTranslateX(view.getTranslateX() + velocity.getX() * deltaTime);
            view.setTranslateY(view.getTranslateY() + velocity.getY() * deltaTime);
        }
        collisionShape.setTranslateX(collisionShape.getTranslateX() + velocity.getX() * deltaTime);
        collisionShape.setTranslateY(collisionShape.getTranslateY() + velocity.getY() * deltaTime);
    }

    public void accelerate(Point2D acceleration){
        velocity = (velocity.add(acceleration));
        if (velocity.distance(0,0) > maxVelocity){
            velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public void accelerate(double x, double y){
        Point2D acceleration = new Point2D(x,y);
        if (velocity.add(acceleration).distance(0,0) > maxVelocity){
            velocity = velocity.normalize().multiply(maxVelocity);
        }else{
            velocity = (velocity.add(acceleration));
        }
    }

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

    public Point2D getVelocity(){
        return velocity;
    }

    public double getMaxVelocity(){
        return maxVelocity;
    }

    public Point2D getForwardVector(){
        return new Point2D(Math.cos(view[0].getRotate() * degToRadConst), Math.sin(view[0].getRotate() * degToRadConst));
    }

    public double getRotation(){
        return view[0].getRotate() * degToRadConst;
    }

    public Collision getCollision(GameObject object){
        Shape resultShape = Shape.intersect(collisionShape, object.getCollisionShape());

        Collision collision = new Collision(!resultShape.getBoundsInLocal().isEmpty(), resultShape.getBoundsInLocal().getMaxX(),
                resultShape.getBoundsInLocal().getMaxY());

        return collision;
    }

    public Collision getCollision(Shape shape){
        Shape resultShape = Shape.intersect(collisionShape,shape);

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
