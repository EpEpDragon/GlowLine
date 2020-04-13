import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Particle {
    private double deltaTime;
    private GraphicsContext gc;

    private double x;
    private double y;
    private int radius;
    private Color color;
    private Point2D velocity;
    private double life = 1;
    private double decay;

    Particle(double x, double y, int radius, Color color, Point2D velocity, double decay, double deltaTime) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.color = color;
        this.velocity = velocity;
        this.decay = decay;
        this.deltaTime = deltaTime;

        gc = ApplicationStart.getGc();
    }

    public void render(){
        gc.setFill(color);
        gc.setGlobalAlpha(life);
        gc.fillOval(x,y,radius,radius);
    }

    public void update(){
        x += velocity.getX()*deltaTime;
        y += velocity.getY()*deltaTime;

        //Bounce off floor
        if(y >= ApplicationStart.resolutionY - 20 - radius){
            y = ApplicationStart.resolutionY - 20 - radius;
            velocity = new Point2D(velocity.getX(), velocity.getY()*-Math.abs(Math.sin(velocity.getX()/velocity.distance(0,0))));
        }
        life -= decay * deltaTime;
    }

    public boolean isDead(){
        if (life <= 0){
            return true;
        }else{
            return false;
        }
    }
}
