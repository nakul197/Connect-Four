package com.kunal.edu;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Controller implements Initializable{

    private static final int ROWS = 6;
    private static final int COLUMS = 7;
    private static final int DISCRADIUS = 80;
    private static final String PlayerOneDiscColour = "#FF0800";
    private static final String PlayerTwoDiscColour = "#FFFFC2";

    private boolean isPlayerOneTurn = true;

    private static String PlayerOneName = "Player One";
    private static String PlayerTwoName = "Player Two";

    private Disc [][] InsertedDisc = new Disc[ROWS][COLUMS];

    private boolean isAllowedToInsert = true;

    @FXML
    public GridPane rootGridPane;

    @FXML
    public Pane InsertedDiscPane;

    @FXML
    public Label PlayerNameLabel;

    @FXML
    public TextField playerOneTextField, playerTwoTextField;

    @FXML
    public Button setNamesButton;


    public void createplayground(){
        setNamesButton.setOnAction(event -> {
            String playerOneName = playerOneTextField.getText();
            String playerTwoName = playerTwoTextField.getText();
            PlayerOneName = playerOneName;
            PlayerTwoName = playerTwoName;
        });
        Shape rectangle_with_holes = CreateGridStruct();
        List<Rectangle> rectangle_click_list = create_clickable_rectangle();
        rootGridPane.add(rectangle_with_holes,0,1);
        for (Rectangle rec:rectangle_click_list) {
            rootGridPane.add(rec,0,1);
        }
    }

    private Shape CreateGridStruct(){
        Shape rectangle_with_holes = new Rectangle((COLUMS+1) * DISCRADIUS,(ROWS+1)*DISCRADIUS);

        for (int row = 0; row < ROWS; row++) {
            for (int col = 0; col < COLUMS; col++) {
                Circle circle = new Circle();
                circle.setRadius(DISCRADIUS/2);
                circle.setCenterX(DISCRADIUS/2);
                circle.setCenterY(DISCRADIUS/2);

                circle.setTranslateX(col * (DISCRADIUS+5) + DISCRADIUS/4);
                circle.setTranslateY(row * (DISCRADIUS+5) + DISCRADIUS/4);

                circle.setSmooth(true);

                rectangle_with_holes = Shape.subtract(rectangle_with_holes,circle);
            }
        }

        rectangle_with_holes.setFill(Color.WHITE);

        return rectangle_with_holes;
    }

    private List<Rectangle> create_clickable_rectangle(){
        List<Rectangle> rectangleArrayList = new ArrayList<>();
        for (int col = 0; col < COLUMS; col++) {
            Rectangle rectangle = new Rectangle(DISCRADIUS,(ROWS + 1) * DISCRADIUS);
            rectangle.setFill(Color.TRANSPARENT);
            rectangle.setTranslateX(col * (DISCRADIUS+5) + DISCRADIUS/4);

            rectangle.setOnMouseEntered(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    rectangle.setFill(Color.valueOf("#eeeeee26"));
                }
            });

            rectangle.setOnMouseExited(new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    rectangle.setFill(Color.TRANSPARENT);
                }
            });

            final int column = col;

            rectangle.setOnMouseClicked(event -> {
                if (isAllowedToInsert){
                isAllowedToInsert = false;
                insertdisc(new Disc(isPlayerOneTurn),column);
                }
            });

            rectangleArrayList.add(rectangle);
        }
        return rectangleArrayList;
    }

    private void insertdisc(Disc disc , int colum){

        int row = ROWS -1;
        while (row >= 0){
            if (getDiscIfPresent(row,colum) == null)
                break;
            row--;
        }
        if (row<0){
            return;
        }
        InsertedDisc[row][colum] = disc;
        InsertedDiscPane.getChildren().add(disc);
        disc.setTranslateX(colum * (DISCRADIUS+5) + DISCRADIUS/4);
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(0.5),disc);
        translateTransition.setToY(row * (DISCRADIUS+5) + DISCRADIUS/4);
        int finalRow = row;
        translateTransition.setOnFinished(event -> {
            isAllowedToInsert = true;
            if (gameEnded(finalRow,colum)){
                gameOver();
                return;
            }

            isPlayerOneTurn = !isPlayerOneTurn;

            PlayerNameLabel.setText(isPlayerOneTurn?PlayerOneName:PlayerTwoName);

        });
        translateTransition.play();
    }

    private boolean gameEnded(int row, int column) {

        List <Point2D> verticalPoints = IntStream.rangeClosed(row - 3,row + 3)
                .mapToObj(r -> new Point2D(r , column))
                .collect(Collectors.toList());

        List <Point2D> horizontalPoints = IntStream.rangeClosed(column - 3,column + 3)
                .mapToObj(col -> new Point2D(row , col))
                .collect(Collectors.toList());

        Point2D startPoint1 = new Point2D(row - 3,column + 3);
        List <Point2D> DiagonalPoints = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint1.add(i,-i))
                .collect(Collectors.toList());

        Point2D startPoint2 = new Point2D(row - 3,column - 3);
        List <Point2D> Diagonal2Points = IntStream.rangeClosed(0,6)
                .mapToObj(i -> startPoint2.add(i,i))
                .collect(Collectors.toList());

        boolean isEnded = checkCombinations(verticalPoints) || checkCombinations(horizontalPoints) || checkCombinations(DiagonalPoints) || checkCombinations(Diagonal2Points);

        return isEnded;
    }

    private boolean checkCombinations(List<Point2D> points) {

        int chain = 0;

        for (Point2D point : points) {

            int rowIndexForArray = (int) point.getX();
            int columnIndexForArray = (int) point.getY();

            Disc disc = getDiscIfPresent(rowIndexForArray, columnIndexForArray);

            if (disc != null && disc.IsPlayerOneMove == isPlayerOneTurn) {  // if the last inserted Disc belongs to the current player

                chain++;
                if (chain == 4) {
                    return true;
                }
            } else {
                chain = 0;
            }
        }

        return false;
    }

    private Disc getDiscIfPresent(int row , int column){
        if (row >= ROWS || row < 0 || column >= COLUMS || column < 0)
            return null;

        return InsertedDisc[row][column];
    }

    private void gameOver(){
        String winner = isPlayerOneTurn?PlayerOneName:PlayerTwoName;

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Connect four");
        alert.setHeaderText("Winner is " + winner);
        alert.setContentText("want to play again");

        ButtonType yesButton = new ButtonType("Yes!");
        ButtonType noButton = new ButtonType("No!,Exit");
        alert.getButtonTypes().setAll(yesButton,noButton);


        Platform.runLater(()->{
            Optional<ButtonType> btnClicked = alert.showAndWait();

            if (btnClicked.isPresent() && btnClicked.get() == yesButton){
                resetGame();
            }
            if (btnClicked.isPresent() && btnClicked.get() == noButton){
                Platform.exit();
                System.exit(0);
            }
        });
    }

    public void resetGame() {
        InsertedDiscPane.getChildren().clear();
        for (int row = 0; row < InsertedDisc.length; row++) {
            for (int column = 0; column < InsertedDisc[row].length; column++) {
                InsertedDisc[row][column] = null;
            }
        }

        isPlayerOneTurn = true;
        PlayerNameLabel.setText(PlayerOneName);

        createplayground();
    }


    private static class Disc extends Circle{

        private final boolean IsPlayerOneMove;

        public Disc(boolean IsPlayerOneMove){
            this.IsPlayerOneMove = IsPlayerOneMove;

            setRadius(DISCRADIUS/2);
            setFill(IsPlayerOneMove?Color.valueOf(PlayerOneDiscColour):Color.valueOf(PlayerTwoDiscColour));
            setCenterX(DISCRADIUS/2);
            setCenterY(DISCRADIUS/2);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }
}
