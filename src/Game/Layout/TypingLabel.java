package Game.Layout;

import javafx.animation.Transition;
import javafx.scene.control.Label;
import javafx.util.Duration;

public class TypingLabel extends Label {
    private static String text;

    private Transition typing;
    private static boolean finished = false;

    TypingLabel(String text, int letterPerSecond) {
        super();
        this.text = text;
        int durationMilli = Math.round((float) text.length()/letterPerSecond * 1000);

        //Typing anim
        typing = new Transition() {
            {
                setCycleCount(1);
                setCycleDuration(new Duration(durationMilli));
            }

            @Override
            protected void interpolate(double frac) {
                finished = false;
                final int length = text.length();
                int currentLength = Math.round(length * (float) frac);
                setText(text.substring(0, currentLength));
            }
        };

        typing.setOnFinished(e -> finished = true);
        typing.play();
    }

    public void play() { typing.playFrom(new Duration(0)); }
    public boolean isFinished(){ return finished; }
}
