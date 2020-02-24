public class MaxMath {
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

    public static void face(double[] subjectX, double[] subjectY, double[] focus){
        double rotDeg;

        double[] centre = {0,0};
        for (int x = 0; x < subjectX.length; x++) {
            centre[0] += subjectX[x];
        }
        for (int y = 0; y < subjectY.length; y++) {
            centre[1] += subjectY[y];
        }
        MaxMath.divArrayDouble(centre, subjectY.length, centre);

        double deltaY = focus[1]-centre[1];
        double deltaX = focus[0]-centre[0];

        rotDeg = Math.atan((deltaY) / (deltaX));

        if (deltaY < 0 && rotDeg > 0) {
            rotDeg = rotDeg - Math.PI;
        }
        else if(deltaY > 0 && rotDeg < 0){
            rotDeg = rotDeg + Math.PI;
        }
        System.out.println(rotDeg);

        double[] tempX = subjectX;
        double[] tempY = subjectY;
        for (int i = 0; i < tempX.length; i++) {
            subjectX[i] = tempX[i] * Math.cos(rotDeg) - tempY[i] * Math.sin(rotDeg);
            subjectY[i] = tempX[i] * Math.sin(rotDeg) + tempY[i] * Math.cos(rotDeg);
        }
    }

}
