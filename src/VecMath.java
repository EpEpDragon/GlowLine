public class VecMath {
    //TODO make Vector type
    //TODO rewrite all vec math functions, too bloody ugly
    public static void incArray(double inc, double[] arr, double[] Out){
        for (int i = 0; i < arr.length; i++){
            Out[i] = arr[i] + inc;
        }
    }

    public static void sumArray(double[] arr1, double[] arr2, double[] Out) {
        for (int i = 0; i < arr1.length; i++) {
            Out[i] = arr1[i] + arr2[i];
        }
    }

    public static void divArrayDouble(double[] arr1, double div, double[] Out){
        double[] temp = arr1;
        for (int i = 0; i < temp.length; i++) {
            Out[i] = temp[i]/div;
        }
    }

    //Find angle between two points (0 to PI / to -PI)
    public static double deltaAngle(double xSub, double ySub, double xFoc, double yFoc){
        double rotDeg;
        double deltaY = yFoc-ySub;
        double deltaX = xFoc-xSub;
        rotDeg = Math.atan((deltaY) / (deltaX));

        if (deltaY < 0 && rotDeg > 0) {
            rotDeg = rotDeg - Math.PI;
        }
        else if(deltaY > 0 && rotDeg < 0){
            rotDeg = rotDeg + Math.PI;
        }
        return rotDeg;
    }

    public static double length(double[] vec){
        return Math.sqrt(vec[0]*vec[0] + vec[1]*vec[1]);
    }
    public static double[] normalize(double[] vec){
        double[] unit = vec;

        unit[0] = vec[0]/length(vec);
        unit[1] = vec[1]/length(vec);
        return unit;
    }

    public static double[] rotate(double subjectX, double subjectY, double rot){
        double tempX = subjectX;
        double tempY = subjectY;

        tempX = subjectX * Math.cos(rot) - subjectY * Math.sin(rot);
        tempY = subjectX * Math.sin(rot) + subjectY * Math.cos(rot);

        double[] rotated = {tempX, tempY};
        return rotated;
    }

    //TODO finish toLocal func
    public static double[] toLocal(double[] globalX, double[] globalY, double[] globalPos){
        double[] localX = globalX;
        double[] localY = globalY;
        for (int i = 0; i < globalX.length; i++) {
            globalX[i] = globalX[i] - globalPos[0];
            globalY[i] = globalY[i] - globalPos[1];
        }
        return localX;
    }
}
