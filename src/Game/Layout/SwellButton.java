package Game.Layout;

import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class SwellButton extends Button {
    private static double growScale = 1.3;
    private static int durationMilliGrow = 300;
    private static int durationMilliUnfold = 800;
    private static int getDurationMilliFade = 200;
    private static double currentScale;

    private Transition unfold;
    private static boolean finished = false;

    SwellButton(String text, int width, boolean shouldUnfold){
        super(text);

        //Unfold
        if (shouldUnfold) {
            unfold = new Transition() {
                {
                    setCycleCount(1);
                    setCycleDuration(new Duration(durationMilliUnfold));
                }

                @Override
                protected void interpolate(double frac) {
                    setStyle("-fx-text-fill: rgba(255, 255, 255, 0)");
                    finished = false;
                    setMaxWidth(width * frac);
                }
            };

            //Fade in
            Transition fade = new Transition() {
                {
                    setCycleCount(1);
                    setCycleDuration(new Duration(getDurationMilliFade));
                }

                @Override
                protected void interpolate(double frac) {
                    setStyle("-fx-text-fill: rgba(255, 255, 255, " + frac + ")");
                }
            };

            unfold.setOnFinished(e -> fade.play());
            fade.setOnFinished(e -> finished = true);
            unfold.play();
        } else { finished = true; }
        //Swell on mouse move
        ScaleTransition grow = new ScaleTransition(Duration.millis(durationMilliGrow), this);
        grow.setCycleCount(1);

        setOnMouseEntered(e -> {
            currentScale = getScaleX();
            grow.stop();
            System.out.println("Enter: " + getScaleX());

            grow.setToX(growScale);
            grow.setToY(growScale);
            grow.setFromX(currentScale);
            grow.setFromY(currentScale);

            grow.play();
            grow.setOnFinished(g -> grow.stop());

        });

        setOnMouseExited(e -> {
            currentScale = getScaleX();
            grow.stop();

            System.out.println("Exit: " + getScaleX());
            grow.setToX(1);
            grow.setToY(1);
            grow.setFromX(currentScale);
            grow.setFromY(currentScale);

            grow.play();
            grow.setOnFinished(g -> grow.stop());
        });
    }

    public void play() {
        if (unfold != null) {
            unfold.playFrom(new Duration(0));
        }
    }

    public boolean isFinished(){ return finished; }
}
