package MainGame.Objects;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.geometry.Point2D;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import static MainGame.MainGame.getResolutionX;
import MainGame.Objects.Collision;

public abstract class GameObject {
    private Node[] view;
    private Shape collisionShape;
    private Point2D velocity = new Point2D(0, 0);
    private double maxVelocity;
    private boolean dead = false;
    private String type;

    /************************************************************
     ▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼Constructors▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼▼
     ************************************************************/

    //For one shape, stroke color independent
    GameObject(double maxVelocity, Color strokeColor, Color fillColor, String type, Node view) {
        this.view = new Node[1];
        this.view[0] = view;
        this.view[0].setMouseTransparent(true);
        this.collisionShape = (Shape) view;
        this.collisionShape.setMouseTransparent(true);

        //if polygon
        if (view instanceof javafx.scene.shape.Polygon) {
            ((Polygon) view).setStroke(strokeColor);
            ((Polygon) view).setFill(fillColor);
        }
        //if circle
        if (view instanceof javafx.scene.shape.Circle) {
            ((Circle) view).setStroke(strokeColor);
            ((Circle) view).setFill(fillColor);
        }

        this.maxVelocity = maxVelocity;
        this.type = type;
    }

    //For one shape
    GameObject(double maxVelocity, String type, Node view) {
        this.view = new Node[1];
        this.view[0] = view;
        this.view[0].setMouseTransparent(true);
        this.collisionShape = (Shape) view;
        this.collisionShape.setMouseTransparent(true);
        this.maxVelocity = maxVelocity;
        this.type = type;
    }

    //For multiple shapes, stroke color independent
    GameObject(double maxVelocity, Color strokeColor, Color fillColor, String type, Node collisionShape, Node... views) {
        this.view = new Node[views.length];
        for (int i = 0; i < views.length; i++) {
            this.view[i] = views[i];
            this.view[i].setMouseTransparent(true);

            //if polygon
            if (view[i] instanceof javafx.scene.shape.Polygon) {
                ((Polygon) view[i]).setStroke(strokeColor);
                ((Polygon) view[i]).setFill(fillColor);
            }
            //if circle
            if (view[i] instanceof javafx.scene.shape.Circle) {
                ((Circle) view[i]).setStroke(strokeColor);
                ((Circle) view[i]).setFill(fillColor);
            }
        }
        this.collisionShape = (Shape) collisionShape;
        this.collisionShape.setMouseTransparent(true);
        this.maxVelocity = maxVelocity;
        this.type = type;
    }

    //For multiple shapes
    GameObject(double maxVelocity, String type, Node collisionShape, Node... views) {
        this.view = new Node[views.length];
        for (int i = 0; i < views.length; i++) {
            this.view[i] = views[i];
            this.view[i].setMouseTransparent(true);
        }
        this.collisionShape = (Shape) collisionShape;
        this.collisionShape.setMouseTransparent(true);
        this.maxVelocity = maxVelocity;
        this.type = type;
    }

    /************************************************************
     ▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲Constructors▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲▲
     ************************************************************/

    public Node[] getView() {
        return view;
    }

    public String getType() {
        return type;
    }

    public Shape getCollisionShape() {
        return collisionShape;
    }

    public void setRotation(double angle) {
        for (Node view : this.view) {
            view.setRotate(Math.toDegrees(angle));
        }
    }

    public double getX() {
        return view[0].getTranslateX();
    }

    public double getY() {
        return view[0].getTranslateY();
    }

    public void setDead() {
        this.dead = true;
    }

    public boolean isDead() {
        return dead;
    }

    public void update(double deltaTime) {
        for (Node view : this.view) {
            view.setTranslateX(view.getTranslateX() + velocity.getX() * deltaTime);
            view.setTranslateY(view.getTranslateY() + velocity.getY() * deltaTime);

            //Teleport to other side of screen if off-screen
            if (view.getTranslateX() < getResolutionX() * -0.01) {
                view.setTranslateX(getResolutionX() + getResolutionX() * 0.01);
                collisionShape.setTranslateX(getResolutionX() + getResolutionX() * 0.01);
            } else if (view.getTranslateX() > getResolutionX() + getResolutionX() * 0.01) {
                view.setTranslateX(getResolutionX() * -0.01);
                collisionShape.setTranslateX(getResolutionX() * -0.01);
            }
        }
        collisionShape.setTranslateX(collisionShape.getTranslateX() + velocity.getX() * deltaTime);
        collisionShape.setTranslateY(collisionShape.getTranslateY() + velocity.getY() * deltaTime);

        handelCollisions(deltaTime);
    }

    public void accelerate(Point2D acceleration) {
        velocity = (velocity.add(acceleration));
        if (velocity.distance(0, 0) > maxVelocity) {
            velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public void accelerate(double x, double y) {
        Point2D acceleration = new Point2D(x, y);
        if (velocity.add(acceleration).distance(0, 0) > maxVelocity) {
            velocity = velocity.normalize().multiply(maxVelocity);
        } else {
            velocity = (velocity.add(acceleration));
        }
    }

    public void setVelocity(Point2D velocity) {
        if (velocity.distance(0, 0) <= maxVelocity) {
            this.velocity = velocity;
        } else {
            this.velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public void setVelocity(double x, double y) {
        Point2D velocity = new Point2D(x, y);
        if (x * x + y * y <= maxVelocity * maxVelocity) {
            this.velocity = velocity;
        } else {
            this.velocity = velocity.normalize().multiply(maxVelocity);
        }
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public void setMaxVelocity(double MaxVelocity) {
        this.maxVelocity = MaxVelocity;
    }

    public double getMaxVelocity() {
        return maxVelocity;
    }

    public Point2D getForwardVector() {
        return new Point2D(Math.cos(Math.toRadians(view[0].getRotate())), Math.sin(Math.toRadians(view[0].getRotate())));
    }

    public double getRotation() {
        return Math.toRadians(view[0].getRotate());
    }

    public void setPolygonFillColour(Color newColour) {
        ((Polygon) view[0]).setFill(newColour);
    }

    public void setRectColour(Color newColour) {
        ((Rectangle) view[0]).setFill(newColour);
    }

    public void setCircleBorder() {
        view[0].setStyle("-fx-stroke: transparent; -fx-stroke-width: 40");
    }

    public Collision getCollision(GameObject object) {
        Shape resultShape = Shape.intersect(collisionShape, object.getCollisionShape());
        return new Collision(!resultShape.getBoundsInLocal().isEmpty(), resultShape.getBoundsInLocal().getMaxX(),
                resultShape.getBoundsInLocal().getMaxY());
    }

    public Collision getCollision(Shape shape) {
        Shape resultShape = Shape.intersect(collisionShape, shape);
        return new Collision(!resultShape.getBoundsInLocal().isEmpty(), resultShape.getBoundsInLocal().getMaxX(),
                resultShape.getBoundsInLocal().getMaxY());
    }

    abstract void handelCollisions(double deltaTime);
}
