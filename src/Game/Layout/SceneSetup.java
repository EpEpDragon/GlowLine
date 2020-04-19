package Game.Layout;

import Game.ApplicationStart;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay, controlsMenu;

    public static Scene createMainMenu(JFXPanel fxPanel) {

        Label title = new Label("Glow Line");
        title.setId("title");
        title.setPadding(new Insets(0, 0, getResolutionY() * 0.5, 0));

        //Start button
        Button start = new Button("Start");
        start.setId("menuButtons");
        start.setOnAction(e -> {
            //Gameplay setup
            createRound();
            fxPanel.setScene(gameplay);
        });

        //Controls Button
        Button controls = new Button("Controls");
        controls.setId("menuButtons");
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
        });

        //Quit Button
        Button quit = new Button("Quit");
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
        Button back = new Button("Back");
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
        //Pause menu
        Button resume = new Button("Resume");
        Button toMain = new Button("Quit to main menu");
        System.out.println(toMain.getWidth());
        System.out.println(toMain.getHeight());
        VBox layout = new VBox(resume, toMain);
        layout.setPadding(new Insets(resolutionY * 0.5 - 29, 0, 0, resolutionX * 0.5 - 158));
        layout.setVisible(false);

        layout.setSpacing(20);
        layout.setAlignment(Pos.CENTER);

        toMain.setOnAction(e -> {
            timer = null;
            //Pause menu is index 0
            root.getChildren().remove(1, root.getChildren().size());
            getBullets().clear();
            getEnemies().clear();
            getEnemyBullets().clear();
            getEmitters().clear();
            fxPanel.setScene(mainMenu);
            layout.setVisible(false);
            fxPanel.setScene(mainMenu);
        });

        resume.setOnAction(e -> {
            System.out.println(toMain.getWidth());
            System.out.println(toMain.getHeight());
            layout.setVisible(false);
            timer.start();
        });

        root.getChildren().add(layout);
        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM1080.css");
        gameplay.setCursor(Cursor.CROSSHAIR);

        System.out.println(toMain.getWidth());
        System.out.println(toMain.getHeight());

        return gameplay;
    }
}
