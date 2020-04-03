import javafx.geometry.Point2D;

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

    public static Point2D unitVecTo(double xFrom, double yFrom, double xTo, double yTo){
        double deltaAngle = relativeDeltaAngle(xFrom, yFrom, xTo, yTo, true);
        return new Point2D(Math.cos(deltaAngle), Math.sin(deltaAngle));
    }

    public static Point2D rotateVec(double x, double y, double rad){
        return new Point2D(x*Math.cos(rad) - y*Math.sin(rad), x*Math.sin(rad) + y*Math.cos(rad));
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

        if(relative.getY() <= 0){
            missileVu *= -1;
        }

        double missileVt = Math.sqrt(missileS*missileS - missileVu*missileVu);

        if (relative.getY() <= 0){
            missileVt *= -1;
        }

        double rad;


        if (relative.getY() >= 0){
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) - Math.PI/2;

        }else{
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) + Math.PI/2;
        }
        Point2D rotated = rotateVec(missileVu, missileVt, rad);
        return rotated;
    }

    public static double clamp(double toClamp, double low, double high){
        if (toClamp < low){ return low; }
        if (toClamp > high){ return high; }
        return toClamp;
    }
}
