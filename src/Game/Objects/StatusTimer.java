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
        printHighScores();
    }

    public boolean isRunning() {
        return isRunning;
    }
}
