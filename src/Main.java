import edu.princeton.cs.introcs.StdDraw;
import java.awt.*;

public class Main {
    public static void  main(String[] args){
        StdDraw.enableDoubleBuffering();
        StdDraw.setCanvasSize();

        //Time between frame in milli seconds (10 is 100 fps)
        //TODO make deltaT dynamic, based on actual draw rate
        int deltaTm = 10;
        double deltaTs = deltaTm*0.0001;

        //Player start position
        double pX = 0.0;
        double pY = 0.0;

        //Game loop
        while (true) {
            StdDraw.clear(Color.BLACK);

            //Input
            //TODO wrap input in input function
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

            //Angle that player should be rotated to face mouse
            double rot = VecMath.deltaAngle(pX, pY, StdDraw.mouseX(), StdDraw.mouseY());
            System.out.println(rot);
            drawPlayer(pX, pY, rot);
            
            StdDraw.show();
            StdDraw.pause(deltaTm);
        }
    }

    private static void drawPlayer(double xPos, double yPos, double rot) {
        StdDraw.setPenColor(Color.white);

        //Player dimensions offset to centre at 0,0
        double[] xCoords = {-0.02, 0.03, -0.02};
        double[] yCoords = {0.02, 0.0, -0.02};

        double[] xCoordsOut = xCoords;
        double[] yCoordsOut = yCoords;

        //Rotate player coords to face mouse
        for (int i = 0; i < xCoords.length; i++) {
            double[] rotated = VecMath.rotate(xCoords[i], yCoords[i], rot);
            //This is to accommodate StdDraws weird inputs
            //TODO find better way to handle StdDraw's inputs
            xCoordsOut[i] = rotated[0];
            yCoordsOut[i] = rotated[1];
        }

        //NaN when mouse not on canvas
        //Move player coords to where it needs to be
        if (!Double.isNaN(xCoordsOut[0])) {
            VecMath.incArray(xPos, xCoords, xCoordsOut);
            VecMath.incArray(yPos, yCoords, yCoordsOut);

            StdDraw.polygon(xCoordsOut, yCoordsOut);
        }
    }
}

