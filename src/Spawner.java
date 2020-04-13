import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;

public abstract class Spawner extends ApplicationStart{
    //Lander spawn time
    static final int landerSpawnTime = 2;
    static double previousTimeLander = landerSpawnTime;

    //Kamikaze spawn time
    static final int kamikazeSpawnTime = 3;
    static double previousTimeKamikaze = kamikazeSpawnTime;

    public static void spawnPass(double time){
        //Lander spawn logic
//        if (time - previousTimeLander >= landerSpawnTime){
//            addGameObject(new Lander(scale), OwnMath.clamp(resolutionX * Math.random(),resolutionX * 0.1, resolutionX * 0.9),resolutionY * -0.1);
//            previousTimeLander = time;
//        }

        //Kamikaze spawn logic
//        if (time - previousTimeKamikaze >= kamikazeSpawnTime && time > 5){
//            addGameObject(new Kamikaze(scale), OwnMath.clamp(resolutionX * Math.random(),resolutionX * 0.1, resolutionX * 0.9),resolutionY * -0.1);
//            previousTimeKamikaze = time;
//        }
    }

    public static void addGameObject(GameObject object, double x, double y) {
        spawnGameObject(object, x, y);
        switch (object.getType()) {
            case "bullet":
                getBullets().add(object);
                break;
            case "enemyBullet":
            case "kamikaze":
                getEnemyBullets().add(object);
                break;
            case "enemy":
                getEnemies().add(object);
                break;
        }
    }

    public static void spawnGameObject(GameObject object, double x, double y) {
        for(Node view : object.getView()) {
            view.setTranslateX(x);
            view.setTranslateY(y);

            getRoot().getChildren().add(view);
        }
        object.getCollisionShape().setTranslateX(x);
        object.getCollisionShape().setTranslateY(y);
    }

    /*************************************************
     * Game Objects
     *************************************************/

    public static class Player extends GameObject {
        //px/s
        private int acceleration = 800;

        Player(double scale) {
            super(500, Color.WHEAT, Color.BLACK, "ally", new Polygon(20*scale, 0*scale, -18*scale, 18*scale, -18*scale, -18*scale));
        }

        @Override
        public void update(double deltaTime) {
            super.update(deltaTime);
            for(Node view : getView()) {
                setRotation(OwnMath.relativeDeltaAngle(view.getTranslateX(), view.getTranslateY(), getMouseX(), getMouseY(), true));

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
        private int acceleration = 900;
        Kamikaze(double scale){
            super(900*scale, Color.WHITE, Color.BLACK, "kamikaze", new Circle(18*scale, Color.TRANSPARENT),
                    new Circle(20*scale, Color.TRANSPARENT),
                    new Circle(15*scale, Color.TRANSPARENT),
                    new Circle(10*scale, Color.TRANSPARENT));
        }

        @Override
        public void update(double deltaTime) {
            super.update(deltaTime);
            //Accelerate to player
            Point2D interceptVec = OwnMath.findInterceptVector(new Point2D(getView()[0].getTranslateX(), getView()[0].getTranslateY()), new Point2D(getPlayer().getView()[0].getTranslateX(), getPlayer().getView()[0].getTranslateY()), getVelocity(), getPlayer().getVelocity(), getMaxVelocity()).normalize();
            accelerate(interceptVec.multiply(acceleration*deltaTime));
        }
    }

    public static class Lander extends GameObject{
        Lander(double scale){
            super(50*scale,"enemy", new Rectangle(30*scale,40*scale, Color.INDIANRED));
            setVelocity(0,30);
        }
    }

    public static class Bullet extends GameObject {
        Bullet(double scale, String type) {
            super(1000*scale, type, new Circle(6*scale, Color.BURLYWOOD));
            setVelocity(getPlayer().getForwardVector().multiply(800).add(getPlayer().getVelocity()));
        }
    }
}
