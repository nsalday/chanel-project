// package com.example.chanel;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;

public class Menu {
    private Stage stage;
    private Scene menuScene;
    private Scene gameScene;
    private PlayerController playerController;

    public Menu(Stage stage, Scene gameScene) {
        this.stage = stage;
        this.gameScene = gameScene;

        VBox layout = new VBox(20);
        layout.setAlignment(Pos.CENTER);

        InputStream imageStream = getClass().getResourceAsStream("/title-bg.png");
        Image backgroundImage = new Image(imageStream);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(background));


        Text titleLabel = new Text("Are you sure you want to exit?\n");
        titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/Minecraft.ttf"), 20));
        titleLabel.setFill(Color.WHITE);

        Text exitButton = createClickableText("Exit", true, () -> exitGame());

        layout.getChildren().addAll(titleLabel, exitButton);
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

    private Text createClickableText(String text, boolean clickable, Runnable onClick) {
        Text clickableText = new Text(text);
        clickableText.setFont(Font.loadFont(getClass().getResourceAsStream("/Minecraft.ttf"), 18));
        clickableText.setFill(Color.LIGHTGRAY);

        if (clickable) {
            clickableText.setOnMouseClicked(e -> onClick.run());

            clickableText.setOnMouseEntered(e -> {
                clickableText.setFill(Color.WHITE);
                clickableText.setText("\u25B6 " + text);
            });

            clickableText.setOnMouseExited(e -> {
                clickableText.setFill(Color.LIGHTGRAY);
                clickableText.setText(text);
            });
        }

        return clickableText;
    }

//    private void continueGame() {
//        hide();
//        if (playerController != null) {
//            playerController.resumeGame();
//        }
//    }

    private void exitGame() {
        System.exit(0);
    }
}
