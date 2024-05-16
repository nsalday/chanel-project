// package com.example.chanel;

import javafx.animation.*;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.effect.DropShadow;
import javafx.util.Duration;

import java.io.InputStream;

public class TitleScreen {
    private Stage primaryStage;

    public void start() {
        primaryStage = new Stage();
        primaryStage.setTitle("Galaxy Dash");

        VBox layout = new VBox(20); // Adding spacing of 20 between nodes
        layout.setAlignment(Pos.CENTER);

        InputStream imageStream = getClass().getResourceAsStream("resources/images/title-bg.png");
        Image backgroundImage = new Image(imageStream);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        layout.setBackground(new Background(background));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5); // Increase the radius for a more pronounced effect
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(173, 31, 44, 1));

        Text titleLabel = new Text("Galaxy Dash\n");
        titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Mario-Kart-DS.ttf"), 48));
        titleLabel.setFill(Color.web("#F76335"));
        titleLabel.setEffect(dropShadow);


        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(0.5), titleLabel);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.8);
        fadeTransition.setAutoReverse(true);
        fadeTransition.setCycleCount(Timeline.INDEFINITE);

        fadeTransition.play();

        Text startText = createClickableText("Start Game", true, () -> startGame());
        startText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        startText.setFill(Color.LIGHTGRAY);

        Text exitText = createClickableText("Exit", true, () -> exitGame());
        exitText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        exitText.setFill(Color.WHITE);

        Text multiplayerText = createClickableText("Multiplayer", true, () -> startMultiplayer());
        multiplayerText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        multiplayerText.setFill(Color.WHITE);

        layout.getChildren().addAll(titleLabel, startText, exitText, multiplayerText);
        Scene titleScene = new Scene(layout, 400, 400);

        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    private Text createClickableText(String text, boolean clickable, Runnable onClick) {
        Text clickableText = new Text(text);
        clickableText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
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

    private void startGame() {
        // Handle start game action
        System.out.println("Starting game...");
        Game game = new Game();

        Stage gameStage = new Stage();
        game.start(gameStage);
    }

    // lobby for multiplayer
    private void startMultiplayer(){
        System.out.println("Starting multiplayer...");
        Multiplayer multiplayer = new Multiplayer();

        Stage multiplayerStage = new Stage();
        multiplayer.start(multiplayerStage);
    
    }

    private void exitGame() {
        // Handle exit game action
        System.out.println("Exiting game...");
        System.exit(0);
    }
}
