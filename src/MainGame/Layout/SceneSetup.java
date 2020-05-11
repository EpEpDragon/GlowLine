package MainGame.Layout;

import MainGame.ApplicationStart;
import javafx.embed.swing.JFXPanel;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.TextAlignment;

import java.io.*;

import static javafx.scene.paint.Color.*;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay, controlsMenu;
    static private Label score = new Label();
    static private Label time = new Label();
    static private Label highScores = new Label();
    static private Rectangle timeDilationBar = new Rectangle(170, 40, LIGHTGREEN);
    static private SwellButton restart = new SwellButton("Restart", 500, true);
    static final double difficultyMaxDeviation = 0.6;
    static private Slider difficulty = new Slider(1-difficultyMaxDeviation, 1+difficultyMaxDeviation, 1);
    static private TextField enterName = new TextField();
    static private boolean readyToResume = false;
    static private javafx.scene.image.Image background;
    static {
        try {
            background = new Image(new FileInputStream("src/MainGame/menuBackground.jpg"));
        } catch (FileNotFoundException ignore) {
        }
    }

    static private ImageView imageView = new ImageView(background);
    public static int rootChildren = 10;

    public static Scene createMainMenu(JFXPanel fxPanel) {
        int buttonWidth = 400;

        TypingLabel title = new TypingLabel("Glow Line", 8);
        title.setId("title");
        title.setPadding(new Insets(0, 0, getResolutionY() * 0.5 + 140, 0));

        //Start button
        SwellButton start = new SwellButton("Start", buttonWidth, true);

        start.setOnAction(e -> {
            //Gameplay setup
            stopMainMenuSong();
            createRound();
            fxPanel.setScene(gameplay);
        });

        //Controls Button
        SwellButton controls = new SwellButton("Controls & Settings", buttonWidth, true);
        controls.setOnAction(e -> {
            fxPanel.setScene(controlsMenu);
            playAll((Pane) controlsMenu.getRoot());
        });

        //Quit Button
        SwellButton quit = new SwellButton("Quit", buttonWidth, true);
        quit.setOnAction(e -> {
            System.exit(0);
        });

        Label highScoreLabel = new Label();
        highScoreLabel.setTextAlignment(TextAlignment.CENTER);
        highScoreLabel.setAlignment(Pos.CENTER);
        highScoreLabel.setText("High Scores:");
        highScoreLabel.setStyle("-fx-font-size: 50px");

        highScores.setTextAlignment(TextAlignment.CENTER);
        highScores.setAlignment(Pos.CENTER);
        highScores.setMaxWidth(498);
        highScores.setMinWidth(498);

        ScrollPane highScoresPane = new ScrollPane();
        highScoresPane.setContent(highScores);
        highScoresPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        highScoresPane.setMaxWidth(500);
        highScoresPane.setMinWidth(500);
        highScoresPane.setMinHeight(140);
        highScoresPane.setMaxHeight(140);

        setupImage();

        //Main menu layout
        VBox menuButtons = new VBox(start, controls, quit, highScoreLabel, highScoresPane);
        menuButtons.setSpacing(resolutionY * 0.02);
        menuButtons.setFillWidth(true);
        menuButtons.setAlignment(Pos.CENTER);
        menuButtons.setPadding(new Insets(resolutionY/3.6, 0, 0, 0));

        StackPane menuLayout = new StackPane(imageView, title, menuButtons);
        menuLayout.setAlignment(Pos.CENTER);

        mainMenu = new Scene(menuLayout, resolutionX, resolutionY, Color.BLACK);
        mainMenu.getStylesheets().add("MainGame/Layout/GLM1080.css");

        return mainMenu;
    }

    public static Scene createControls(JFXPanel fxPanel) {
        //Thrust
        Label moveA = new Label("Left/right (level 1):");
        Label moveB = new Label("A/D");
        moveB.setId("controlLabel");

        //Left, right
        Label thrustA = new Label("Thrust (level 2):");
        Label thrustB = new Label("W");
        thrustB.setId("controlLabel");

        //Aim
        Label aimA = new Label("Aim:");
        Label aimB = new Label("Mouse");
        aimB.setId("controlLabel");

        //Time dilation
        Label dilateA = new Label("Time dilation:");
        Label dilateB = new Label("Space");
        dilateB.setId("controlLabel");

        //Back button
        SwellButton back = new SwellButton("Back", 0, false);
        back.setId("menuButtons");
        back.setOnAction(e -> {
            fxPanel.setScene(mainMenu);
        });

        Label difficultyLabel = new Label("Set difficulty:");
        Label difficultyExplanation = new Label("This effects the starting difficulty, as well as the\nrate at which the difficulty increases in level 2.");
        difficultyExplanation.setStyle("-fx-font-size: 15px; -fx-text-alignment: center");
        difficultyExplanation.setPadding(new Insets(-5,0,5,0));

        //Layout
        Label title = new Label("Controls");
        title.setId("subTitle");
        title.setPadding(new Insets(-30,0,0,0));

        GridPane controls = new GridPane();
        controls.setVgap(15);
        controls.setHgap(20);
        controls.setAlignment(Pos.CENTER);

        controls.add(moveA, 0, 0);
        controls.add(moveB, 1, 0);

        controls.add(thrustA, 0, 1);
        controls.add(thrustB, 1, 1);

        controls.add(aimA, 0, 2);
        controls.add(aimB, 1, 2);

        controls.add(dilateA, 0, 3);
        controls.add(dilateB, 1, 3);

        difficulty.setMaxWidth(300);
        difficulty.setStyle("-fx-fill: black");
        difficulty.setOnMouseReleased(e ->{
            setOriginalDifficulty(difficulty.getValue());
        });

        VBox difficultyLayout = new VBox(difficultyLabel, difficultyExplanation, difficulty);
        difficultyLayout.setSpacing(10);
        difficultyLayout.setAlignment(Pos.CENTER);
        VBox layout = new VBox(title, controls, difficultyLayout, back);
        layout.setSpacing(resolutionY * 0.05);
        layout.setAlignment(Pos.CENTER);

        controlsMenu = new Scene(layout);
        controlsMenu.getStylesheets().add("MainGame/Layout/GLM1080.css");
        return controlsMenu;
    }

    public static Scene createGameplay(JFXPanel fxPanel, Pane root) {
        /**Pause menu**/
        SwellButton resume = new SwellButton("Resume", 400, true);
        SwellButton toMain = new SwellButton("Quit to main menu", 400, true);

        VBox pauseMenu = new VBox(resume, toMain);
        pauseMenu.setSpacing(20);
        pauseMenu.setAlignment(Pos.CENTER);
        pauseMenu.setMinWidth(resolutionX);
        pauseMenu.setMinHeight(resolutionY);
        pauseMenu.setVisible(false);

        toMain.setOnAction(e -> {
            if (resume.isFinished()) {
                stopGamePlaySongs();
                startMainMenuSong();
                pauseMenu.setVisible(false);
                timer.stop();
                gameplayElements.getChildren().remove(0, gameplayElements.getChildren().size());
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
        Label gameOver = new Label("GAME");
        Label gameOver2 = new Label("OVER");
        gameOver.setId("gameOver");
        gameOver2.setId("gameOver");
        gameOver.setPadding(new Insets(-100,0,-110,0));
        gameOver2.setPadding(new Insets(0,0,-40,0));

        enterName.setMaxWidth(500);
        //remove focus from enterName so that it doesn't fill with spaces if user was using time dilation when gameover
        enterName.setFocusTraversable(false);
        enterName.setOnMousePressed(e -> {
            setEnteringName(true);
        });
        enterName.setOnAction(e -> {
            saveScore();
            setSavedScore(true);
        });

        VBox gameOverMenu = new VBox(gameOver, gameOver2, restart, toMain2, enterName);
        gameOverMenu.setMinWidth(resolutionX);
        gameOverMenu.setMinHeight(resolutionY);
        gameOverMenu.setSpacing(20);
        gameOverMenu.setAlignment(Pos.CENTER);
        gameOverMenu.setVisible(false);

        //remove focus from button so that it doesn't automatically press button if user was using time dilation when gameover
        toMain2.setFocusTraversable(false);
        toMain2.setOnAction(e -> {
            if (restart.isFinished()) {
                if (!getSavedScore()){
                    saveScore();
                }
                stopGamePlaySongs();
                startMainMenuSong();
                setGameOverVisible(false);
                timer.stop();
                gameplayElements.getChildren().remove(0, gameplayElements.getChildren().size());
                clearStuff();
                fxPanel.setScene(mainMenu);
                playAll((Pane) mainMenu.getRoot());
            }
        });

        //remove focus from button so that it doesn't automatically press button if user was using time dilation when gameover
        restart.setFocusTraversable(false);
        restart.setOnAction(e -> {
                    if (restart.isFinished()) {
                        if (!getSavedScore()){
                            saveScore();
                        }
                        setGameOverVisible(false);
                        timer.stop();
                        gameplayElements.getChildren().remove(0, gameplayElements.getChildren().size());
                        clearStuff();
                        createRound();
                    }
                }
        );

        /**Intermediate Info**/
        Label intermediateLevelUp = new Label("LEVEL UP");
        intermediateLevelUp.setStyle("-fx-font-size: 250px; -fx-text-alignment: center");
        Label levelInfo = new Label("You are now free to move anywhere: aim with your mouse and use <W> to go there...\nOh, and don't forget to use <SPACEBAR> to help you! ;)");
        levelInfo.setStyle("-fx-font-size: 30px; -fx-text-alignment: center");

        VBox intermediateInfo = new VBox(intermediateLevelUp, levelInfo);
        intermediateInfo.setMinWidth(resolutionX);
        intermediateInfo.setMinHeight(resolutionY);
        intermediateInfo.setSpacing(20);
        intermediateInfo.setAlignment(Pos.CENTER);
        intermediateInfo.setVisible(false);

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
        root.getChildren().add(0, gameplayElements);
        root.getChildren().add(1, time);
        root.getChildren().add(2, score);
        root.getChildren().add(3, timeDilationBar);
        root.getChildren().add(4, life1);
        root.getChildren().add(5, life2);
        root.getChildren().add(6, life3);
        root.getChildren().add(7, gameOverDarken);
        root.getChildren().add(8, timeDilation);
        root.getChildren().add(9, gameOverMenu);
        root.getChildren().add(10, intermediateInfo);
        root.getChildren().add(11, pauseMenu);

        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("MainGame/Layout/GLM1080.css");
        gameplay.setCursor(Cursor.CROSSHAIR);

        return gameplay;
    }

    private static void setupImage() {
        imageView.setX(0);
        imageView.setY(0);
        imageView.setFitHeight(resolutionY);
        imageView.setFitWidth(resolutionX);
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
                int difficultyPercentage = (int) (100*((getOriginalDifficulty()-1+difficultyMaxDeviation)/(difficultyMaxDeviation*2)));
                if (enterName.getText().equals("")) {
                    bufferedWriter.write("Unknown User, " + difficultyPercentage + "%^" + getCurrentScore());
                } else {
                    bufferedWriter.write(enterName.getText() + ", " + difficultyPercentage + "%^" + getCurrentScore());
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
        printHighScores();
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
        try {
            FileReader lineCounter = new FileReader(highScoreFileName);
            BufferedReader bufferedCounter = new BufferedReader(lineCounter);

            int lines = 0;
            while (bufferedCounter.readLine() != null) lines++;
            lineCounter.close();

            String[] names = new String[lines];
            int[] scores = new int[lines];

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
            if (highScores.length() > 1) {
                SceneSetup.highScores.setText(highScores.substring(1));
            }
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
