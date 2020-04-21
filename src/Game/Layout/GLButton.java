package Game.Layout;

import javafx.animation.ScaleTransition;
import javafx.scene.control.Button;
import javafx.util.Duration;

public class GLButton extends Button {
    double growScale = 1.3;
    int durationMilli = 300;
    double currentScale;
    GLButton(String text){
        super(text);
        ScaleTransition grow = new ScaleTransition(Duration.millis(durationMilli), this);
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
}
