import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Emitter {
    List<Particle> particles = new ArrayList<>();
    int spawnRate;
    int particleSpeed;
    Color color;
    double decay;

    Emitter(int spawnRate, int particleSpeed, Color color, double lifetime){
        this.spawnRate = spawnRate;
        this.particleSpeed = particleSpeed;
        this.color = color;
        this.decay = 1/lifetime;
    }

    //Directional emitter
    public void emit(double x, double y, double direction, Point2D systemVelocity, double spread, double deltaTime) {
        for (int i = 0; i < spawnRate * deltaTime; i++) {
            double rotation = direction + (Math.random() - 0.5)*spread*2;
            Point2D velocity = new Point2D(Math.cos(rotation), Math.sin(rotation)).multiply(particleSpeed*Math.random()*0.8+0.8);
            velocity = velocity.add(systemVelocity);
            velocity = OwnMath.rotateVec(velocity, direction);
            particles.add(new Particle(x, y, 10, color, velocity, decay, deltaTime));
        }
    }

    public void emit(double x, double y, Point2D systemVelocity, double direction, double spread, double taperRate, double deltaTime) {
        for (int i = 0; i < spawnRate * deltaTime; i++) {
            double deltaRotation = (Math.random() - 0.5)*spread*2;
            double rotation = direction + deltaRotation;

            Point2D velocity = new Point2D(Math.cos(rotation), Math.sin(rotation)).multiply((particleSpeed-Math.abs((Math.atan(deltaRotation*taperRate)*particleSpeed)))*Math.random()*0.8+0.8);
            velocity = velocity.add(systemVelocity);
            System.out.println(velocity);
            particles.add(new Particle(x, y, 10, color, velocity, decay, deltaTime));
        }
    }

    //Circular emitter
    public void emit(double x, double y, Point2D systemVelocity, double deltaTime){
        double rotation = Math.random() * 2 * Math.PI;
        Point2D velocity = new Point2D(Math.cos(rotation), Math.sin(rotation));
        velocity = velocity.multiply(particleSpeed*Math.random()*0.8+0.8);
        velocity = velocity.add(systemVelocity);
        for (int i = 0; i < spawnRate * deltaTime; i++) {
            particles.add(new Particle(x, y, 10, color, velocity, decay, deltaTime));
        }
    }

    public void update(){
        for(Iterator<Particle> it = particles.iterator(); it.hasNext();){
            Particle p = it.next();
            p.update();
            p.render();

            if(p.isDead()){
                it.remove();
            }
        }
    }
}
