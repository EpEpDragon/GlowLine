package Game.Layout;

import Game.ApplicationStart;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public abstract class SceneSetup extends ApplicationStart {
    static private Scene mainMenu, gameplay;

    public static Scene createMainMenu(JFXPanel fxPanel){
        //Main menu buttons
        Button start = new Button("Start");
        start.setOnAction(e -> {
            //Gameplay setup
            createRound();
            fxPanel.setScene(gameplay);
        });

        //Main menu layout
        BorderPane mainMenuLayout = new BorderPane(start);
        mainMenu = new Scene(mainMenuLayout, resolutionX, resolutionY, Color.BLACK);
        mainMenu.getStylesheets().add("Game/Layout/GLM.css");

        return mainMenu;
    }

    public static Scene createGameplay(JFXPanel fxPanel, Pane root){
        gameplay = new Scene(root, resolutionX, resolutionY, Color.BLACK);
        gameplay.getStylesheets().add("Game/Layout/GLM.css");
        //Initial scene
        fxPanel.setScene(mainMenu);

        gameplay.setCursor(Cursor.CROSSHAIR);
        return gameplay;
    }
}
