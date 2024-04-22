package com.example.chanel;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Menu {
    private Stage stage;
    private Scene menuScene;
    private Scene gameScene; // Store the game scene
    private PlayerController playerController;

    public Menu(Stage stage, Scene gameScene) {
        this.stage = stage;
        this.gameScene = gameScene; // Store the game scene for later use

        // Create buttons
        Button playButton = new Button("Play");
        playButton.setOnAction(e -> play());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> stage.close());

        // Layout
        VBox layout = new VBox(10);
        layout.getChildren().addAll(playButton, exitButton);
        menuScene = new Scene(layout, 400, 400);
    }

    public void setPlayerController(PlayerController playerController) {
        this.playerController = playerController;
    }

    public void show() {
        stage.setScene(menuScene);
    }

    public void hide() {
        stage.setScene(gameScene);
    }

    private void play() {
        hide();
        if (playerController != null) {
            playerController.resumeGame();
        }
    }
}
