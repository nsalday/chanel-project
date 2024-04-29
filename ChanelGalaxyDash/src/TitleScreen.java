package com.example.chanel;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TitleScreen {
    private Stage primaryStage;

    public void start() {
        primaryStage = new Stage();
        primaryStage.setTitle("Galaxy Dash");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);

        Label titleLabel = new Label("Galaxy Dash");
        titleLabel.setStyle("-fx-font-size: 24pt; -fx-font-weight: bold;");

        Button startButton = new Button("Start Game");
        startButton.setOnAction(e -> startGame());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(titleLabel, startButton, exitButton);
        Scene titleScene = new Scene(layout, 400, 400);

        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    private void startGame() {
        primaryStage.close();

        Game game = new Game();

        Stage gameStage = new Stage();
        game.start(gameStage);
    }
}
