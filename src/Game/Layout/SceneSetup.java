package Game.Layout;

import Game.ApplicationStart;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import javax.swing.*;
import java.awt.*;
import java.io.*;

import static javafx.scene.paint.Color.*;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay, controlsMenu;
    static private Label score = new Label();
    static private Label time = new Label();
    static private Label highScores = new Label();
    static private Rectangle timeDilationBar = new Rectangle(170, 40, LIGHTGREEN);
    static private SwellButton restart = new SwellButton("Restart", 500, true);
    static private TextField enterName = new TextField();
    static private boolean readyToResume = false;
    public static int rootChildren = 10;

    public static Scene createMainMenu(JFXPanel fxPanel) {
        int buttonWidth = 200;

        TypingLabel title = new TypingLabel("Glow Line", 8);
        title.setId("title");
        title.setPadding(new Insets(0, 0, getResolutionY() * 0.5, 0));

        //Start button
        SwellButton start = new SwellButton("Start", buttonWidth, true);

        start.setId("menuButtons");
        start.setOnAction(e -> {
            //Gameplay setup
            stopMainMenuSong();
            createRound();
            fxPanel.setScene(gameplay);
        });

        //Controls Button
        SwellButton controls = new SwellButton("Controls", buttonWidth, true);
        controls.setId("menuButtons");
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
            playAll((Pane) controlsMenu.getRoot());
        });

        //Quit Button
        SwellButton quit = new SwellButton("Quit", buttonWidth, true);
        quit.setId("menuButtons");
        quit.setOnAction(e -> {
            System.exit(0);
        });

        highScores.setTextAlignment(TextAlignment.CENTER);
        highScores.setAlignment(Pos.CENTER);
        highScores.setMaxWidth(498);
        highScores.setMinWidth(498);

        ScrollPane highScoresPane = new ScrollPane();
        highScoresPane.setContent(highScores);
        highScoresPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        highScoresPane.setMaxWidth(500);
        highScoresPane.setMinWidth(500);
        highScoresPane.setMinHeight(315);
        highScoresPane.setMaxHeight(315);

        //Main menu layout
        VBox menuButtons = new VBox(start, controls, quit, highScoresPane);
        menuButtons.setSpacing(resolutionY * 0.03);
        menuButtons.setFillWidth(true);
        menuButtons.setAlignment(Pos.CENTER);
        menuButtons.setPadding(new Insets(280, 0, 0, 0));

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
        SwellButton resume = new SwellButton("Resume", 400, true);
        SwellButton toMain = new SwellButton("Quit to main menu", 400, true);

        VBox pauseMenu = new VBox(resume, toMain);
        pauseMenu.setPadding(new Insets(resolutionY * 0.5 - 29, 0, 0, resolutionX * 0.5 - 158));
        pauseMenu.setSpacing(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setVisible(false);

        toMain.setOnAction(e -> {
            if (resume.isFinished()) {
                stopGamePlaySongs();
                startMainMenuSong();
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
        SwellButton toMain2 = new SwellButton("Quit to main menu", 500, true);
        Label gameOver = new Label("GAME\nOVER");
        gameOver.setId("gameOver");

        enterName.setMaxWidth(500);
        //remove focus from enterName so that it doesn't fill with spaces if user was using time dilation when gameover
        enterName.setFocusTraversable(false);
        enterName.setOnMousePressed(e -> {
            setEnteringName(true);
        });
        enterName.setOnAction(e -> {
            saveScore();
        });

        VBox gameOverMenu = new VBox(gameOver, restart, toMain2, enterName);
        gameOverMenu.setPadding(new Insets(resolutionY * 0.5 - 450, 0, 0, resolutionX * 0.5 - 270));
        gameOverMenu.setSpacing(20);
        gameOverMenu.setAlignment(Pos.CENTER);
        gameOverMenu.setVisible(false);

        //remove focus from button so that it doesn't automatically press button if user was using time dilation when gameover
        toMain2.setFocusTraversable(false);
        toMain2.setOnAction(e -> {
            if (restart.isFinished()) {
                stopGamePlaySongs();
                startMainMenuSong();
                setGameOverVisible(false);
                timer.stop();
                //Pause menu is index 0, HUD index 1, gameOver menu index 2
                root.getChildren().remove(rootChildren, root.getChildren().size());
                clearStuff();
                fxPanel.setScene(mainMenu);
                playAll((Pane) mainMenu.getRoot());
            }
        });

        //remove focus from button so that it doesn't automatically press button if user was using time dilation when gameover
        restart.setFocusTraversable(false);
        restart.setOnAction(e -> {
                    if (restart.isFinished()) {
                        setGameOverVisible(false);
                        timer.stop();
                        //Pause menu is index 0, HUD index 1, gameOver menu index 2
                        root.getChildren().remove(rootChildren, root.getChildren().size());
                        clearStuff();
                        createRound();
                        //unnecessary: fxPanel.setScene(gameplay);
                    }
                }
        );


        /**HUD**/
        //Timer
        time.setStyle("-fx-font-size: 50px;-fx-padding: 10px 0px 0px " + Integer.toString(resolutionX - 140) + "px;");
        score.setStyle("-fx-font-size: 50px;-fx-padding: 10px 0px 0px 15px;");

        //time dilation and gameOverMenu rectangular transparent overlays
        Rectangle gameOverDarken = new Rectangle(resolutionX, resolutionY, Color.valueOf("#000000"));
        gameOverDarken.setOpacity(0.4);
        gameOverDarken.setVisible(false);

        Rectangle timeDilation = new Rectangle(resolutionX, resolutionY, Color.valueOf("#CCFFCA"));
        timeDilation.setOpacity(0.3);
        timeDilation.setVisible(false);

        //time dilation bar
        timeDilationBar.setX(20);
        timeDilationBar.setY(90);

        //life hearts
        SVGPath life1 = new SVGPath();
        SVGPath life2 = new SVGPath();
        SVGPath life3 = new SVGPath();
        String SVGShape = "M23.6,0c-3.4,0-6.3,2.7-7.6,5.6C14.7,2.7,11.8,0,8.4,0C3.8,0,0,3.8,0,8.4c0,9.4,9.5,11.9,16,21.6c6.1-9.3,16-12.1,16-21.2C32,3.8,28.2,0,23.6,0z";
        life1.setContent(SVGShape);
        life2.setContent(SVGShape);
        life3.setContent(SVGShape);
        String lifeStyle = "-fx-fill: indianred; -fx-stroke: transparent; -fx-translate-y: 150; -fx-translate-x: ";
        life1.setStyle(lifeStyle + "20");
        life2.setStyle(lifeStyle + "80");
        life3.setStyle(lifeStyle + "140");

        //Do not change order, if adding another one, change rootChildren
        root.getChildren().add(0, pauseMenu);
        root.getChildren().add(1, time);
        root.getChildren().add(2, score);
        root.getChildren().add(3, timeDilationBar);
        root.getChildren().add(4, life1);
        root.getChildren().add(5, life2);
        root.getChildren().add(6, life3);
        root.getChildren().add(7, gameOverDarken);
        root.getChildren().add(8, timeDilation);
        root.getChildren().add(9, gameOverMenu);

        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM1080.css");
        gameplay.setCursor(Cursor.CROSSHAIR);

        return gameplay;
    }

    public static void resetNameEnter() {
        enterName.setPromptText("Enter name here, then hit enter...");
        enterName.setText("");
        enterName.editableProperty().setValue(true);
    }

    public static void saveScore() {
        if (enterName.getText().contains("^")) {
            enterName.setText("");
            enterName.setPromptText("^ not allowed, try again.");
        } else {
            try {
                FileWriter writer = new FileWriter(highScoreFileName, true);
                BufferedWriter bufferedWriter = new BufferedWriter(writer);
                bufferedWriter.newLine();
                if (enterName.getText().equals("")) {
                    bufferedWriter.write("Unknown User" + "^" + getCurrentScore());
                } else {
                    bufferedWriter.write(enterName.getText() + "^" + getCurrentScore());
                }
                bufferedWriter.close();
                enterName.setText("");
                enterName.setPromptText("NAME SAVED!");
                enterName.editableProperty().setValue(false);
            } catch (IOException ignored) {
                enterName.setText("");
                enterName.setPromptText("Could not write to file");
            }
        }
    }

    public static void clearStuff() {
        getBullets().clear();
        getEnemies().clear();
        getEnemyBullets().clear();
        getEmitters().clear();
    }

    public static void setTimeDilationFraction(double fraction){
        timeDilationBar.setWidth(fraction*170);
    }

    public static void setTimeDilationColor(Color barColor){
        timeDilationBar.setFill(barColor);
    }

    public static void playAll(Pane pane) {
        for (Node node : pane.getChildren()) {
            if (node instanceof Pane) {
                playAll((Pane) node);
            } else if (node instanceof TypingLabel) {
                ((TypingLabel) node).play();
            } else if (node instanceof SwellButton) {
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

    public static void updateRestartBtn(String newText) {
        restart.setText(newText);
    }

    public static void printHighScores() {
        String[] names = new String[1000];
        int[] scores = new int[1000];
        try {
            FileReader reader = new FileReader(highScoreFileName);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String currentLine;
            int counter = 0;
            while ((currentLine = bufferedReader.readLine()) != null) {
                if (currentLine.contains("^")) {
                    int indexer = currentLine.indexOf("^");
                    names[counter] = currentLine.substring(0, indexer);
                    String score = currentLine.substring(indexer + 1);
                    scores[counter] = Integer.parseInt(score);
                    counter += 1;
                }
            }

            //sort array
            for (int j = 0; j < counter; j++) {
                for (int q = 0; q < counter - 1; q++) {
                    if (scores[q] < scores[q + 1]) {
                        int temp;
                        temp = scores[q];
                        scores[q] = scores[q + 1];
                        scores[q + 1] = temp;
                        String tempS;
                        tempS = names[q];
                        names[q] = names[q + 1];
                        names[q + 1] = tempS;
                    }
                }
            }

            String highScores = "";
            for (int i = 0; i < counter; i++) {
                highScores = highScores + "\n" + names[i] + " - " + scores[i];
            }
            reader.close();
            SceneSetup.highScores.setText("High Scores:" + highScores);
        } catch (IOException ignored) {
            SceneSetup.highScores.setText("File not found...");
        }
    }

    public static boolean isReadyToResume() {
        return readyToResume;
    }

    public static void setReadyToResume(boolean isResume) {
        readyToResume = isResume;
    }
}
