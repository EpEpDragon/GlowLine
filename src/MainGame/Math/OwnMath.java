package MainGame.Math;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class OwnMath {

    //Find angle between two points (0 to PI / to -PI)
    public static double relativeDeltaAngle(double xFrom, double yFrom, double xTo, double yTo, boolean halfScale) {
        double rotDeg;
        double deltaY = yTo - yFrom;
        double deltaX = xTo - xFrom;

        //System.out.println("DX: " + deltaX + " DY: " + deltaY);
        rotDeg = Math.atan((deltaY) / (deltaX));
        if (halfScale) {
            if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg - Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = rotDeg + Math.PI;
            }
        } else {
            if (deltaY < 0 && rotDeg < 0) {
                rotDeg = 2 * Math.PI - Math.abs(rotDeg);
            } else if (deltaY < 0 && rotDeg > 0) {
                rotDeg = rotDeg + Math.PI;
            } else if (deltaY > 0 && rotDeg < 0) {
                rotDeg = Math.PI + Math.abs(rotDeg);
            }
        }
        return rotDeg;
    }

    public static double relativeDeltaAngle(Point2D from, Point2D to, boolean halfScale) {
        return relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), halfScale);
    }

    public static double lerp(double start, double end, double fraction) {
        return start + fraction * (end - start);
    }

    public static Color colorLerp(Color colorS, Color colorE, double fraction) {
        double sH = colorS.getHue();
        double sS = colorS.getSaturation();
        double sB = colorS.getBrightness();

        double eH = colorE.getHue();
        double eS = colorE.getSaturation();
        double eB = colorE.getBrightness();

        return Color.hsb(lerp(sH, eH, fraction), lerp(sS, eS, fraction), lerp(sB, eB, fraction));
    }

    public static int getPlaceValue(int number, int place) {
        return (number % (place * 10) - number % place)/place;
    }

    public static Point2D rotateVec(double x, double y, double rad) {
        return new Point2D(x * Math.cos(rad) - y * Math.sin(rad), x * Math.sin(rad) + y * Math.cos(rad));
    }

    public static double lengthSqrd(Point2D vector) {
        return vector.getY() * vector.getY() + vector.getX() * vector.getX();
    }

    public static Point2D findInterceptVector(Point2D from, Point2D to, Point2D missileV, Point2D targetV, double missileS) {
        Point2D targetRelativeV = targetV.subtract(missileV);
        Point2D relative = to.subtract(from);
        if (Math.round(lengthSqrd(relative)) == 0) {
            return relative;
        }
        Point2D right = new Point2D(relative.getY(), relative.getX() * -1);


        double missileVu = right.normalize().dotProduct(targetRelativeV);
        //Axis system correction
        if (relative.getY() <= 0) {
            missileVu *= -1;
        }

        double missileVt = Math.sqrt(missileS * missileS - missileVu * missileVu);

        //Catch for when missile can not catch player, otherwise missile Vt in NaN and missile vanishes.
        if (Double.isNaN(missileVt)) {
            missileVt = 0;
        }

        //Axis system correction
        if (relative.getY() <= 0) {
            missileVt *= -1;
        }

        double rad;
        if (relative.getY() >= 0) {
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) - Math.PI / 2;
        } else {
            rad = relativeDeltaAngle(from.getX(), from.getY(), to.getX(), to.getY(), true) + Math.PI / 2;
        }

        //rotate missile Vt and missile Vu to original axis system
        return rotateVec(missileVu, missileVt, rad);
    }

    public static double clamp(double toClamp, double low, double high) {
        if (toClamp < low) {
            return low;
        }
        return Math.min(toClamp, high);
    }
}
