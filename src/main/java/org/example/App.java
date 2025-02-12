package org.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.logging.Level;
import java.util.logging.Logger;

public class App extends Application{

    private static final Logger logger = Logger.getLogger(App.class.getName());
    static Game game = new Game();
    private static double xOffset = 0;
    private static double yOffset = 0;

    public static void main(String[] args) {
        LoggerController loggerController = new LoggerController();
        loggerController.disableLoggingForClasses(true);
        game.startGameThread();
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        StartScreen startScreen = new StartScreen();
        Scene startScene = new Scene(startScreen.startDisplay());
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setScene(startScene);
        stage.show();
        startScreen.getMultiplayerButton().setOnMouseClicked(mouseEvent -> {
            logger.log(Level.INFO, "Game mode: multiple players");
            startDisplay(stage, true);
        });

        startScreen.getOnePlayerButton().setOnMouseClicked(mouseEvent -> {
            logger.log(Level.INFO, "Game mode: player vs computer");
            startDisplay(stage, false);
        });
    }

    public void startDisplay(Stage stage,boolean isHuman){
        game.setHuman(isHuman);
        Scene boardScene = new Scene(game.getGameDisplay().display());
        game.getGameController().restartTimer(game.getGameDisplay());
        stage.setScene(boardScene);
        logger.log(Level.INFO, "Game board loaded");
        stage.setX(stage.getX()/4);
        stage.setY(stage.getY()/4);
        stage.show();
        game.getGameDisplay().getPane().setOnMousePressed(event -> {
            xOffset = stage.getX() - event.getScreenX();
            yOffset = stage.getY() - event.getScreenY();
        });
        game.getGameDisplay().getPane().setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() + xOffset);
            stage.setY(event.getScreenY() + yOffset);
        });
    }
}
