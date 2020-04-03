import javafx.geometry.Point2D;

public class OwnMath {

    //Find angle between two points (0 to PI / to -PI)
    public static double deltaAngle(double xFrom, double yFrom, double xTo, double yTo){
        double rotDeg;
        double deltaY = yTo-yFrom;
        double deltaX = xTo-xFrom;

        //System.out.println("DX: " + deltaX + " DY: " + deltaY);
        rotDeg = Math.atan((deltaY) / (deltaX));
        if (deltaY < 0 && rotDeg > 0) {
            rotDeg = rotDeg - Math.PI;
        }
        else if(deltaY > 0 && rotDeg < 0){
            rotDeg = rotDeg + Math.PI;
        }
        return rotDeg;
    }

    public static Point2D unitVecTo(double xFrom, double yFrom, double xTo, double yTo){
        double deltaAngle = deltaAngle(xFrom, yFrom, xTo, yTo);
        return new Point2D(Math.cos(deltaAngle), Math.sin(deltaAngle));
    }

    public static double clamp(double toClamp, double low, double high){
        if (toClamp < low){ return low; }
        if (toClamp > high){ return high; }
        return toClamp;
    }
}
