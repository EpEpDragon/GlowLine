package Game.Effects;

import Game.Objects.GameObject;
import Game.Math.OwnMath;
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
    double radius;
    double particleDecay;
    double emitterDecay;
    double emitterLife = 1;
    double spread;
    double taperRate;
    double speedVariation;
    String direction;
    GameObject owner;

    public Emitter(int spawnRate, int particleSpeed, Color color, double radius, double particleLifetime, String direction, double spread, double taperRate, double speedVariation, double emitterLifetime, GameObject owner){
        this.spawnRate = spawnRate;
        this.particleSpeed = particleSpeed;
        this.color = color;
        this.radius = radius;
        this.particleDecay = 1/particleLifetime;

        if(emitterLifetime == -1){
            this.emitterDecay = 0;
        }else{
            this.emitterDecay = 1/emitterLifetime;
        }

        this.direction = direction;
        this.spread = spread;
        this.taperRate = taperRate;
        this.speedVariation = speedVariation;
        this.owner = owner;
    }

    public void emit(double deltaTime) {
        if (emitterLife >= 0 && (!owner.isDead() || emitterDecay != 0)) {
            for (int i = 0; i < spawnRate * deltaTime; i++) {
                double offset = (Math.random() - 0.5) * spread * 2;
                double rotation = 0;
                switch (direction){
                    case "forward":
                        rotation = owner.getRotation();
                        break;
                    case "backwards":
                        rotation = owner.getRotation()-Math.PI;
                        break;
                    case "velocity":
                        rotation = OwnMath.relativeDeltaAngle(new Point2D(0,0), owner.getVelocity().normalize(), true);
                        break;
                    case "-velocity":
                        rotation = OwnMath.relativeDeltaAngle(new Point2D(0,0), owner.getVelocity().normalize(), true) - Math.PI;
                        break;
                    case "0":
                        rotation = 0;
                        break;
                }
                rotation += offset;

                double taper = Math.abs(Math.atan(offset) * particleSpeed) * taperRate;
                double deltaSpeed = Math.random() * speedVariation + speedVariation;

                Point2D velocity = new Point2D(Math.cos(rotation), Math.sin(rotation));
                velocity = velocity.multiply(OwnMath.clamp(particleSpeed - taper
                        - deltaSpeed * particleSpeed, 0, particleSpeed));
                Point2D v = owner.getVelocity();
                velocity = velocity.add(owner.getVelocity());
                particles.add(new Particle(owner.getX() - radius/2, owner.getY() - radius/2 , (int)radius, color, velocity, particleDecay, deltaTime));
            }
        }
    }

    public void update(double deltaTime){
        for(Iterator<Particle> it = particles.iterator(); it.hasNext();){
            Particle p = it.next();
            p.update();
            p.render();
            if(p.isDead()){
                it.remove();
            }
        }
        emitterLife -= emitterDecay*deltaTime;
    }

    public boolean isDead(){
        if((emitterLife <= 0 || owner.isDead()) && particles.isEmpty()){
            return true;
        }
        return false;
    }
}
