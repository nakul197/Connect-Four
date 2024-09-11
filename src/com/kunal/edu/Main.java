package com.kunal.edu;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {
    private Controller controller;
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Game.fxml"));
        GridPane rootGrid = loader.load();

        controller = loader.getController();
        controller.createplayground();

        Pane menupane = (Pane) rootGrid.getChildren().get(0);
        MenuBar menubar = createMenu();

        menubar.prefWidthProperty().bind(primaryStage.widthProperty());
        menupane.getChildren().add(menubar);


        Scene scene = new Scene(rootGrid);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Connect 4");
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    private MenuBar createMenu(){
        Menu FileMenu = new Menu("File");
        MenuItem newGame = new MenuItem("New Game");

        newGame.setOnAction(event -> resetGame());

        MenuItem ResetGame = new MenuItem("Reset Game");

        ResetGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                resetGame();
            }
        });

        SeparatorMenuItem sepratormenu = new SeparatorMenuItem();
        MenuItem ExitGame = new MenuItem("Exit Game");

        ExitGame.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                exitGame();
            }
        });

        MenuBar menuBar = new MenuBar();

        FileMenu.getItems().addAll(newGame,ResetGame,sepratormenu,ExitGame);

        Menu HelpMenu = new Menu("Help");

        MenuItem aboutconnect4 = new MenuItem("Aboutconnect4");

        aboutconnect4.setOnAction(event -> {
            aboutconnect4();
        });

        SeparatorMenuItem seprator = new SeparatorMenuItem();
        MenuItem AboutMe = new MenuItem("AboutMe");

        AboutMe.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                aboutMe();
            }
        });

        HelpMenu.getItems().addAll(aboutconnect4,seprator,AboutMe);
        menuBar.getMenus().addAll(FileMenu,HelpMenu);

        return menuBar;
    }

    private void aboutMe() {
        Alert Aboutme = new Alert(Alert.AlertType.INFORMATION);
        Aboutme.setTitle("About Me");
        Aboutme.setHeaderText("About Developer ");
        Aboutme.setContentText("I am kunal Chauhan i am the creator of this project i love to code" +
                "Thank you for Playing It <3 :)");
        Aboutme.show();
    }

    private void aboutconnect4() {
        Alert aboutgame = new Alert(Alert.AlertType.INFORMATION);
        aboutgame.setTitle("About Connect 4 game");
        aboutgame.setHeaderText("How to Play");
        aboutgame.setContentText("Connect Four is a two-player connection game in which the players first choose a color" +
                " and then take turns dropping colored discs from the top into a seven-column, six-row vertically " +
                "suspended grid. The pieces fall straight down, occupying the next available space within the column. " +
                "The objective of the game is to be the first to form a horizontal, vertical, or diagonal line of four " +
                "of one's own discs. Connect Four is a solved game. The first player can always win by playing the right" +
                " moves.");
        aboutgame.show();
    }

    private void exitGame() {
        Platform.exit();
        System.exit(0);
    }

    private void resetGame() {
        controller.resetGame();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
