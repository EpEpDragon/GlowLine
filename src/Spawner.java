import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl;

import javax.swing.*;

public class Spawner extends ApplicationStart{
    static int resolutionX;
    static int resolutionY;
    static final int landerSpawnTime = 2;
    static double previousTimeLander = landerSpawnTime;

    Spawner(int resolutionX, int resolutionY){
        this.resolutionX = resolutionX;
        this.resolutionY = resolutionY;
    }

    public static void spawnPass(double time){
        if (time - previousTimeLander >= landerSpawnTime){
            addGameObject(new Lander(scale),"lander", OwnMath.clamp(resolutionX * Math.random(),resolutionX * 0.1, resolutionX * 0.9),resolutionY * -0.1);
            previousTimeLander = time;
        }
    }

    public static void addGameObject(GameObject object, String type, double x, double y) {
        spawnGameObject(object, x, y);
        //  System.out.println(object.getVelocity());
        switch (type) {
            case "bullet":
                getBullets().add(object);
                break;
            case "lander":
                getLanders().add(object);
                break;
        }
    }

    public static void spawnGameObject(GameObject object, double x, double y) {
        for(Node view : object.getView()) {
            view.setTranslateX(x);
            view.setTranslateY(y);

            getRoot().getChildren().add(view);
        }
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public static class Player extends GameObject {
        //px/s
        private int acceleration = 600;

        Player(double scale) {
            super(300, Color.WHEAT, new Polygon(18*scale, 0*scale, -18*scale, 18*scale, -18*scale, -18*scale));
        }

        @Override
        public void update(double deltaTime) {
            super.update(deltaTime);

            for(Node view : getView()) {
                setRotation(OwnMath.deltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY()));

                //Teleport player to other side of screen if off-screen
                if (view.getTranslateX() < resolutionX * -0.01) {
                    view.setTranslateX(resolutionX + resolutionX * 0.01);
                } else if (view.getTranslateX() > resolutionX + resolutionX * 0.01) {
                    view.setTranslateX(resolutionX * -0.01);
                }
            }
        }

        public int getAcceleration() {
            return acceleration;
        }
    }

    public static class Kamikaze extends GameObject{
        Kamikaze(double scale){
            super(280*scale, Color.AZURE, new Circle(15*scale, Color.TRANSPARENT),
                    new Circle(15*scale, Color.TRANSPARENT),
                    new Circle(10*scale, Color.TRANSPARENT),
                    new Circle(8*scale, Color.TRANSPARENT));
        }
    }

    public static class Lander extends GameObject{
        Lander(double scale){
            super(50*scale, new Rectangle(30*scale,40*scale, Color.INDIANRED));
            setVelocity(0,30);
        }
    }

    public static class Bullet extends GameObject {
        Bullet(double scale) {
            super(1000*scale, new Circle(6*scale, Color.BURLYWOOD));
            setVelocity(getPlayer().getForwardVector().multiply(800).add(getPlayer().getVelocity()));
        }
    }
}
