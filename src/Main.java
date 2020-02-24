import edu.princeton.cs.introcs.StdDraw;

import java.lang.management.PlatformLoggingMXBean;

public class Main {
    public static void  main(String[] args){

        int deltaTm = 10;
        double deltaTs = deltaTm*0.0001;
        StdDraw.enableDoubleBuffering();
        double pX = 0.0;
        double pY = 0.0;
        while (true) {
            StdDraw.clear();
            if(StdDraw.isKeyPressed(65)){
                pX -= 10*deltaTs;
            }
            if(StdDraw.isKeyPressed(68)){
                pX += 10*deltaTs;
            }
            if(StdDraw.isKeyPressed(87)) {
                pY += 10*deltaTs;
            }
            if(StdDraw.isKeyPressed(83)){
                pY -= 10*deltaTs;
            }

            drawPlayer(pX, pY);
            StdDraw.show();
            StdDraw.pause(deltaTm);
        }
    }

    private static void drawPlayer(double xPos, double yPos) {
        StdDraw.setPenColor(0, 0, 0);
        double[] xCoords = {0.0, 0.05, 0.1};
        double[] yCoords = {0.0, 0.05, 0.0};

        double[] xCoordsOut = {0.0, 0.0, 0.0};
        double[] yCoordsOut = {0.0, 0.0, 0.0};

        MaxMath.incArray(xPos, xCoords, xCoordsOut);
        MaxMath.incArray(yPos, yCoords, yCoordsOut);

        double[] focus = {StdDraw.mouseX(),StdDraw.mouseY()};
        MaxMath.face(xCoordsOut, yCoordsOut, focus);

        StdDraw.filledPolygon(xCoordsOut, yCoordsOut);
    }
}

