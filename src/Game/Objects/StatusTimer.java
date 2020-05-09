package Game.Objects;

import javafx.animation.AnimationTimer;

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
    }

    public boolean isRunning() {
        return isRunning;
    }
}
