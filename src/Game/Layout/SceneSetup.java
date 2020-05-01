package Game.Layout;

import Game.ApplicationStart;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay, controlsMenu;
    static private Label score = new Label();
    static private Label time = new Label();
    static private boolean readyToResume = false;
    public static int rootChildren  = 4;

    public static Scene createMainMenu(JFXPanel fxPanel) {
        int buttonWidth = 200;

        TypingLabel title = new TypingLabel("Glow Line", 10);
        title.setId("title");
        title.setPadding(new Insets(0, 0, getResolutionY() * 0.5, 0));

        //Start button
        SwellButton start = new SwellButton("Start", buttonWidth, true);

        start.setId("menuButtons");
        start.setOnAction(e -> {
            //Gameplay setup
            createRound();
            fxPanel.setScene(gameplay);
        });

        //Controls Button
        SwellButton controls = new SwellButton("Controls", buttonWidth,true);
        controls.setId("menuButtons");
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
            playAll((Pane) controlsMenu.getRoot());
        });

        //Quit Button
        SwellButton quit = new SwellButton("Quit",buttonWidth,true);
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
        SwellButton back = new SwellButton("Back", 0, false);
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
        SwellButton resume = new SwellButton("Resume", 400,true);
        SwellButton toMain = new SwellButton("Quit to main menu", 400,true);

        VBox pauseMenu = new VBox(resume, toMain);
        pauseMenu.setPadding(new Insets(resolutionY * 0.5 - 29, 0, 0, resolutionX * 0.5 - 158));
        pauseMenu.setSpacing(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setVisible(false);

        toMain.setOnAction(e -> {
            if (resume.isFinished()) {
                pauseMenu.setVisible(false);
                timer.stop();
                //Pause menu is index 0, HUD index 1, gameOver menu index 2
                root.getChildren().remove(rootChildren, root.getChildren().size());
                clearStuff();
                fxPanel.setScene(mainMenu);
                playAll((Pane) mainMenu.getRoot());
            }
        });

        resume.setOnAction(e -> {
            if (resume.isFinished()) {
                pauseMenu.setVisible(false);
                timer.start();
            }
        });

        resume.getFade().setOnFinished(e -> {
            readyToResume = true;
        });

        /**GameOver menu**/
        SwellButton restart = new SwellButton("Restart", 400,true);
        SwellButton toMain2 = new SwellButton("Quit to main menu", 400,true);

        VBox gameOverMenu = new VBox(restart, toMain2);
        gameOverMenu.setPadding(new Insets(resolutionY * 0.5 - 29, 0, 0, resolutionX * 0.5 - 158));
        gameOverMenu.setSpacing(20);
        gameOverMenu.setAlignment(Pos.CENTER);
        gameOverMenu.setVisible(false);

        toMain2.setOnAction(e -> {
            if (restart.isFinished()) {
                gameOverMenu.setVisible(false);
                timer.stop();
                //Pause menu is index 0, HUD index 1, gameOver menu index 2
                root.getChildren().remove(rootChildren, root.getChildren().size());
                clearStuff();
                fxPanel.setScene(mainMenu);
                playAll((Pane) mainMenu.getRoot());
            }
        });

        restart.setOnAction(e -> {
            if (restart.isFinished()) {
                gameOverMenu.setVisible(false);
                timer.stop();
                //Pause menu is index 0, HUD index 1, gameOver menu index 2
                root.getChildren().remove(rootChildren, root.getChildren().size());
                clearStuff();
                createRound();
                //unnecessary: fxPanel.setScene(gameplay);
            }
        });


        /**HUD**/
        //Timer
        time.setStyle("-fx-font-size: 50px;-fx-padding: 10px 0px 0px "+Integer.toString(resolutionX-140)+"px;");
        score.setStyle("-fx-font-size: 50px;-fx-padding: 10px 0px 0px 15px;");

        VBox hud = new VBox(time, score);

        //Do not change order, if adding another one, change rootChildren
        root.getChildren().add(0, pauseMenu);
        root.getChildren().add(1, time);
        root.getChildren().add(2, gameOverMenu);
        root.getChildren().add(3, score);

        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM1080.css");
        gameplay.setCursor(Cursor.CROSSHAIR);

        return gameplay;
    }

    public static void clearStuff() {
        getBullets().clear();
        getEnemies().clear();
        getEnemyBullets().clear();
        getEmitters().clear();
    }

    public static void playAll(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof Pane){
                playAll((Pane) node);
            }else if (node instanceof TypingLabel){
                ((TypingLabel) node).play();
            }else if (node instanceof SwellButton){
                ((SwellButton) node).play();
            }
        }
    }

    public static void updateTime(String time) {
        SceneSetup.time.setText(time);
    }

    public static void updateScore(String score) {
        SceneSetup.score.setText(score);
    }

    public static void updateScoreColour(Color colour) {
        SceneSetup.score.setTextFill(colour);
    }

    public static void resetHUD() {
        SceneSetup.time.setText("00:00");
        SceneSetup.score.setText("Score: 0");
    }

    public static boolean isReadyToResume(){ return readyToResume; }
    public static void setReadyToResume( boolean isResume){ readyToResume = isResume; }
}
