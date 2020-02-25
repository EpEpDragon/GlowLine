import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;

public class Main {
    public static void  main(String[] args){
        StdDraw.enableDoubleBuffering();
        int deltaTm = 10;
        double deltaTs = deltaTm*0.0001;

        double pX = 0.0;
        double pY = 0.0;
        while (true) {
            StdDraw.clear(Color.BLACK);
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

            double rot = VecMath.deltaAngle(pX, pY, StdDraw.mouseX(), StdDraw.mouseY());
            System.out.println(rot);
            drawPlayer(pX, pY, rot);
            StdDraw.show();
            StdDraw.pause(deltaTm);
        }
    }

    private static void drawPlayer(double xPos, double yPos, double rot) {
        StdDraw.setPenColor(Color.white);

        double[] xCoords = {-0.02, 0.03, -0.02};
        double[] yCoords = {0.02, 0.0, -0.02};

        double[] xCoordsOut = xCoords;
        double[] yCoordsOut = yCoords;

        for (int i = 0; i < xCoords.length; i++) {
            double[] rotated = VecMath.rotate(xCoords[i], yCoords[i], rot);
            xCoordsOut[i] = rotated[0];
            yCoordsOut[i] = rotated[1];
        }
        if (!Double.isNaN(xCoordsOut[0])) {
            VecMath.incArray(xPos, xCoords, xCoordsOut);
            VecMath.incArray(yPos, yCoords, yCoordsOut);

            StdDraw.polygon(xCoordsOut, yCoordsOut);
        }
    }
}

