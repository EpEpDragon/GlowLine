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
    double spread;
    double taperRate;
    double speedVariation;

    Emitter(int spawnRate, int particleSpeed, Color color, double lifetime, double spread, double taperRate, double speedVariation){
        this.spawnRate = spawnRate;
        this.particleSpeed = particleSpeed;
        this.color = color;
        this.decay = 1/lifetime;
        this.spread = spread;
        this.taperRate = taperRate;
        this.speedVariation = speedVariation;
    }

    public void emit(double x, double y, double direction, Point2D systemVelocity, double deltaTime) {
        for (int i = 0; i < spawnRate * deltaTime; i++) {
            double offset = (Math.random() - 0.5)*spread*2;
            double rotation = direction + offset;

            double taper = Math.abs(Math.atan(offset)*particleSpeed)*taperRate;
            double deltaSpeed = Math.random()*speedVariation+speedVariation;

            Point2D velocity = new Point2D(Math.cos(rotation), Math.sin(rotation));
            velocity = velocity.multiply(OwnMath.clamp(particleSpeed - taper
                    -deltaSpeed*particleSpeed, 0, particleSpeed));

            velocity = velocity.add(systemVelocity);
            particles.add(new Particle(x, y, 5, color, velocity, decay, deltaTime));
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
