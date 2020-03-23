import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

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
        object.getView().setTranslateX(x);
        object.getView().setTranslateY(y);

        getRoot().getChildren().add(object.getView());
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public static class Player extends GameObject {
        //px/s
        private int acceleration = 600;

        Player(double scale) {
            super(new Polygon(18*scale, 0*scale, -18*scale, 18*scale, -18*scale, -18*scale), 300, Color.WHEAT);
        }

        @Override
        public void update(double deltaTime) {
            super.update(deltaTime);
            setRotation(OwnMath.deltaAngle(getView().getTranslateX(), getView().getTranslateY(), getMouseX(), getMouseY()));
            //Teleport player to other side of screen if off-screen
            if (getView().getTranslateX() < resolutionX*-0.01){
                getView().setTranslateX(resolutionX + resolutionX*0.01);
            }else if (getView().getTranslateX() > resolutionX + resolutionX*0.01){
                getView().setTranslateX(resolutionX*-0.01);
            }
        }

        public int getAcceleration() {
            return acceleration;
        }
    }

    public static class Enemy extends GameObject {
        Enemy(double scale) {
            super(new Polygon(15*scale, 0*scale, -15*scale, 15*scale, -15*scale, -15*scale), 100, Color.AZURE);
        }
    }

    public static class Lander extends GameObject{
        Lander(double scale){
            super(new Rectangle(30*scale,40*scale, Color.INDIANRED),50);
            setVelocity(0,30);
        }
    }

    public static class Bullet extends GameObject {
        Bullet(double scale) {
            super(new Circle(6*scale, Color.BURLYWOOD), 800);
            setVelocity(getPlayer().getForwardVector().multiply(800).add(getPlayer().getVelocity()));
        }
    }
}
