package Game.Math;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class OwnMath {

    //Find angle between two points (0 to PI / to -PI)
    public static double relativeDeltaAngle(double xFrom, double yFrom, double xTo, double yTo, boolean halfScale){
        double rotDeg;
        double deltaY = yTo-yFrom;
        double deltaX = xTo-xFrom;

        //System.out.println("DX: " + deltaX + " DY: " + deltaY);
         rotDeg = Math.atan((deltaY) / (deltaX));
        if(halfScale) {
            if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg - Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = rotDeg + Math.PI;
            }
        }else{
            if (deltaY < 0 && rotDeg < 0){
                rotDeg = 2*Math.PI - Math.abs(rotDeg);
            } else if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg + Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = Math.PI + Math.abs(rotDeg);
            }
        }
        return rotDeg;
    }

    public static double lerp(double start, double end, double fraction)
    {
        return start + fraction * (end - start);
    }

    public static Color colorLerp(Color colorS, Color colorE, double fraction){
        double sH = colorS.getHue();
        double sS = colorS.getSaturation();
        double sB = colorS.getBrightness();

        double eH = colorE.getHue();
        double eS = colorE.getSaturation();
        double eB = colorE.getBrightness();

        return Color.hsb(lerp(sH,eH,fraction), lerp(sS,eS,fraction), lerp(sB,eB,fraction));
    }

    public static double relativeDeltaAngle(Point2D from, Point2D to, boolean halfScale){
        double rotDeg;
        double deltaY = to.getY()-from.getY();
        double deltaX = to.getX()-from.getX();

        //System.out.println("DX: " + deltaX + " DY: " + deltaY);
        rotDeg = Math.atan((deltaY) / (deltaX));
        if(halfScale) {
            if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg - Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = rotDeg + Math.PI;
            }
        }else{
            if (deltaY < 0 && rotDeg < 0){
                rotDeg = 2*Math.PI - Math.abs(rotDeg);
            } else if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg + Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = Math.PI + Math.abs(rotDeg);
            }
        }
        return rotDeg;
    }

    public static Point2D unitVecTo(double xFrom, double yFrom, double xTo, double yTo){
        double deltaAngle = relativeDeltaAngle(xFrom, yFrom, xTo, yTo, true);
        return new Point2D(Math.cos(deltaAngle), Math.sin(deltaAngle));
    }

    public static Point2D rotateVec(double x, double y, double rad){
        return new Point2D(x*Math.cos(rad) - y*Math.sin(rad), x*Math.sin(rad) + y*Math.cos(rad));
    }

    public static Point2D rotateVec(Point2D vec, double rad){
        return new Point2D(vec.getX()*Math.cos(rad) - vec.getY()*Math.sin(rad), vec.getX()*Math.sin(rad) + vec.getY()*Math.cos(rad));
    }

    public static double lengthSqrd(Point2D vector){
        return vector.getY()*vector.getY() + vector.getX()*vector.getX();
    }

    public static Point2D abs(Point2D vector){
        return new Point2D(Math.abs(vector.getX()), Math.abs(vector.getY()));
    }

    public static Point2D findInterceptVector(Point2D from, Point2D to, Point2D missileV, Point2D targetV, double missileS){
        Point2D targetRelativeV = targetV.subtract(missileV);
        Point2D relative = to.subtract(from);
        if (Math.round(lengthSqrd(relative)) == 0){
            return relative;
        }
        Point2D right = new Point2D(relative.getY(), relative.getX() * -1);


        double missileVu = right.normalize().dotProduct(targetRelativeV);
        //Axis system correction
        if(relative.getY() <= 0){
            missileVu *= -1;
        }

        double missileVt = Math.sqrt(missileS*missileS - missileVu*missileVu);

        //Catch for when missile can not catch player, otherwise missile Vt in NaN and missile vanishes.
        if(Double.isNaN(missileVt)){
            missileVt = 0;
        }

        //Axis system correction
        if (relative.getY() <= 0){
            missileVt *= -1;
        }

        double rad;
        if (relative.getY() >= 0){
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) - Math.PI/2;
        }else{
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) + Math.PI/2;
        }

        //rotate missile Vt and missile Vu to original axis system
        Point2D rotated = rotateVec(missileVu, missileVt, rad);
        if(Double.isNaN(rotated.getY())){
            System.out.println("ERROR: NaN");
            System.out.println("from: " + from);
            System.out.println("to: " + to);
            System.out.println("missileV: " + missileV);
            System.out.println("targetV: " + targetV);
            System.out.println("targetRelativeV: " + targetRelativeV);
            System.out.println("relative: " + relative);
            System.out.println("right: " + right);
            System.out.println("missileVu: " + missileVu);
            System.out.println("missileVt: " + missileVt);
            System.out.println("rad: " + rad);
            System.out.println("rotated: " + rotated);
        }
        return rotated;
    }

    public static double clamp(double toClamp, double low, double high){
        if (toClamp < low){ return low; }
        if (toClamp > high){ return high; }
        return toClamp;
    }
}
