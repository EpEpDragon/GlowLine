public class OwnMath {

    //Find angle between two points (0 to PI / to -PI)
    public static double deltaAngle(double xSub, double ySub, double xFoc, double yFoc){
        double rotDeg;
        double deltaY = yFoc-ySub;
        double deltaX = xFoc-xSub;

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

    public static double clamp(double toClamp, double low, double high){
        if (toClamp < low){ return low; }
        if (toClamp > high){ return high; }
        return toClamp;
    }
}
