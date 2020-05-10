package Game.Objects;

import javafx.animation.AnimationTimer;

import static Game.Layout.SceneSetup.printHighScores;

public abstract class StatusTimer extends AnimationTimer {

    private static boolean isRunning;

    @Override
    public void start() {
        super.start();
        isRunning = true;
    }

    @Override
    public void stop() {
        super.stop();
        isRunning = false;
        //This way doesn't have to make sure that all the ways of going back to mainMenu has below command, as stop() is always called.
        printHighScores();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
