package Game.Layout;

import Game.ApplicationStart;
import javafx.application.Platform;
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

    public static Scene createMainMenu(JFXPanel fxPanel){

        Label title = new Label("Glow Line");
        title.setId("title");
        title.setPadding(new Insets(0, 0,getResolutionY()*0.5,0));
        //title.setAlignment(Pos.TOP_CENTER);

        //Main menu buttons

        Button start = new Button("Start");

        start.setOnAction(e -> {
            //Gameplay setup
            createRound();
            fxPanel.setScene(gameplay);
        });

        Button quit = new Button("Quit");

        quit.setOnAction(e -> {
            System.exit(0);
        });

        Button settings = new Button("Settings");

        Button controls = new Button("Controls");
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
        });

        //Main menu layout
        VBox menuButtons = new VBox(start, controls, settings, quit);
        menuButtons.setSpacing(resolutionY*0.03);
        menuButtons.setFillWidth(true);
        menuButtons.setAlignment(Pos.CENTER);

        StackPane menuLayout = new StackPane(title, menuButtons);
        menuLayout.setAlignment(Pos.CENTER);

        mainMenu = new Scene(menuLayout, resolutionX, resolutionY, Color.BLACK);
        mainMenu.getStylesheets().add("Game/Layout/GLM1080.css");

        return mainMenu;
    }

    public static Scene createControls(JFXPanel fxPanel){
        Button back = new Button("Back");
        back.setOnAction(e -> {
            fxPanel.setScene(mainMenu);
        });

        VBox buttons = new VBox(back);
        buttons.setViewOrder(0);
        controlsMenu = new Scene(buttons);
        controlsMenu.getStylesheets().add("Game/Layout/GLM1080.css");
        return controlsMenu;
    }

    public static Scene createGameplay(JFXPanel fxPanel, Pane root){
        Button resume = new Button("Resume");

        Button toMain = new Button("Quit to main menu");


        VBox buttons = new VBox(resume, toMain);
        buttons.setVisible(false);
        buttons.setPadding(new Insets(resolutionY*0.5,0,0,resolutionX*0.5));
        buttons.setAlignment(Pos.CENTER);

        toMain.setOnAction(e -> {
            System.out.println("asfasdf");
            timer = null;
            //Pause menu is index 1
            root.getChildren().remove(1, root.getChildren().size());
            fxPanel.setScene(mainMenu);
            buttons.setVisible(false);
            fxPanel.setScene(mainMenu);
        });

        resume.setOnAction(e -> {
            buttons.setVisible(false);
            timer.start();
            System.out.println("asdasdasdasdasdasdsada");
        });

        root.getChildren().add(buttons);
        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM1080.css");

        gameplay.setCursor(Cursor.CROSSHAIR);

        return gameplay;
    }
}
