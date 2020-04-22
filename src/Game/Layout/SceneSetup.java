package Game.Layout;

import Game.ApplicationStart;
import javafx.animation.ScaleTransition;
import javafx.animation.Transition;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay, controlsMenu;
    static private Label time = new Label();

    public static Scene createMainMenu(JFXPanel fxPanel) {

        Label title = new Label("Glow Line");
        title.setId("title");
        title.setPadding(new Insets(0, 0, getResolutionY() * 0.5, 0));

        //Start button
        GLButton start = new GLButton("Start");

        start.setId("menuButtons");
        start.setOnAction(e -> {
            //Gameplay setup
            createRound();
            fxPanel.setScene(gameplay);
            settingsNewGame();
        });

        //Controls Button
        GLButton controls = new GLButton("Controls");
        controls.setId("menuButtons");
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
        });

        //Quit Button
        GLButton quit = new GLButton("Quit");
        quit.setId("menuButtons");
        quit.setOnAction(e -> {
            System.exit(0);
        });

        //Main menu layout
        VBox menuButtons = new VBox(start, controls, quit);
        menuButtons.setSpacing(resolutionY * 0.03);
        menuButtons.setFillWidth(true);
        menuButtons.setAlignment(Pos.CENTER);

        StackPane menuLayout = new StackPane(title, menuButtons);
        menuLayout.setAlignment(Pos.CENTER);

        mainMenu = new Scene(menuLayout, resolutionX, resolutionY, Color.BLACK);
        mainMenu.getStylesheets().add("Game/Layout/GLM1080.css");

        return mainMenu;
    }


    public static Scene createControls(JFXPanel fxPanel) {
        //Thrust
        Label thrustA = new Label("Thrust:");
        Label thrustB = new Label("W");
        thrustB.setId("controlLabel");

        //Aim
        Label aimA = new Label("Aim:");
        Label aimB = new Label("Mouse");
        aimB.setId("controlLabel");

        //Time dilation
        Label dilateA = new Label("Time dialation:");
        Label dilateB = new Label("Space");
        dilateB.setId("controlLabel");

        //Back button
        GLButton back = new GLButton("Back");
        back.setId("menuButtons");
        back.setOnAction(e -> {
            fxPanel.setScene(mainMenu);
        });

        //Layout
        Label title = new Label("Controls");
        title.setId("subTitle");

        GridPane controls = new GridPane();
        controls.setVgap(15);
        controls.setHgap(20);
        controls.setAlignment(Pos.CENTER);
        controls.add(thrustA, 0, 0);
        controls.add(thrustB, 1, 0);

        controls.add(aimA, 0, 1);
        controls.add(aimB, 1, 1);

        controls.add(dilateA, 0, 2);
        controls.add(dilateB, 1, 2);

        VBox layout = new VBox(title, controls, back);
        layout.setSpacing(85);
        layout.setAlignment(Pos.CENTER);

        controlsMenu = new Scene(layout);
        controlsMenu.getStylesheets().add("Game/Layout/GLM1080.css");
        return controlsMenu;
    }

    public static Scene createGameplay(JFXPanel fxPanel, Pane root) {
        /**Pause menu**/
        GLButton resume = new GLButton("Resume");
        GLButton toMain = new GLButton("Quit to main menu");

        VBox pauseMenu = new VBox(resume, toMain);
        pauseMenu.setPadding(new Insets(resolutionY * 0.5 - 29, 0, 0, resolutionX * 0.5 - 158));
        pauseMenu.setSpacing(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setVisible(false);

        toMain.setOnAction(e -> {
            timer = null;
            //Pause menu is index 0, HUD index 1
            root.getChildren().remove(2, root.getChildren().size());
            getBullets().clear();
            getEnemies().clear();
            getEnemyBullets().clear();
            getEmitters().clear();
            fxPanel.setScene(mainMenu);
            pauseMenu.setVisible(false);
            fxPanel.setScene(mainMenu);
        });

        resume.setOnAction(e -> {
            System.out.println(toMain.getWidth());
            System.out.println(toMain.getHeight());
            pauseMenu.setVisible(false);
            timer.start();
        });

        /**HUD**/
        //Timer
        time.setStyle("-fx-font-size: 50px;-fx-padding: 15px 0px 0px 15px;");

        VBox hud = new VBox(time);

        root.getChildren().add(0, pauseMenu);
        root.getChildren().add(1, time);

        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM1080.css");
        gameplay.setCursor(Cursor.CROSSHAIR);

        System.out.println(toMain.getWidth());
        System.out.println(toMain.getHeight());

        return gameplay;
    }

    public static void updateTime(String time){ SceneSetup.time.setText(time); }
}
