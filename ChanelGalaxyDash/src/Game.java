package com.example.chanel;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Game extends Application {

    private Pane root;
    private ImageView player;
    private ImageView backgroundImageView1;
    private ImageView backgroundImageView2;
    private Random random;
    private PlayerController playerController;
    private AnimationTimer spawnObstaclesTimer;
    private AnimationTimer spawnPowerUpsTimer;
    private AnimationTimer gameLoop;
    private int enemyCount = 1;
    private double enemySpeed = 1;
    private double backgroundPosY = 0;
    private Text livesText;
    private Stage stage;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        root = new Pane();
        Image playerImage = new Image(getClass().getResourceAsStream("/images/player.png"));
        player = new ImageView(playerImage);

        Image backgroundImage = new Image(getClass().getResourceAsStream("/images/space_bg9.png"));
        backgroundImageView1 = new ImageView(backgroundImage);
        backgroundImageView2 = new ImageView(backgroundImage);
        backgroundImageView1.setFitWidth(400);
        backgroundImageView1.setFitHeight(400);
        backgroundImageView2.setFitWidth(400);
        backgroundImageView2.setFitHeight(400);
        root.getChildren().addAll(backgroundImageView1, backgroundImageView2);

        backgroundImageView2.setTranslateY(-backgroundImageView1.getFitHeight() + backgroundPosY);

        root.getChildren().add(player);


        Scene scene = new Scene(root, 400, 400);

        Menu menu = new Menu(primaryStage, scene);

        menu.setPlayerController(playerController);

        playerController = new PlayerController(player, root, scene, 3, this, menu);
        player.setX(180);
        player.setY(300);

        primaryStage.setTitle("Galaxy Dash");
        primaryStage.setScene(scene);
        primaryStage.show();

        AnimationTimer backgroundAnimation = new AnimationTimer() {
            @Override
            public void handle(long now) {

                backgroundPosY += 1;
                backgroundImageView1.setTranslateY(backgroundPosY);
                backgroundImageView2.setTranslateY(-backgroundImageView1.getFitHeight() + backgroundPosY);


                if (backgroundPosY >= backgroundImageView1.getFitHeight()) {
                    backgroundPosY = 0;
                }
            }
        };
        backgroundAnimation.start();

        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                livesText.setText("lives: " + playerController.getHealth());

            }
        };
        gameLoop.start();

        random = new Random();
        startSpawningPowerUps();
        startSpawningObstacles();
        startGameLoop();

        livesText = new Text();
        livesText.setFill(Color.WHITE);
        InputStream fontStream = getClass().getResourceAsStream("/fonts/Minecraft.ttf");
        Font livesFont = Font.loadFont(fontStream, 16);
        livesText.setFont(livesFont);
        livesText.setX(scene.getWidth() - 80);
        livesText.setY(20);
        root.getChildren().add(livesText);


    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollisions();
                moveObstacles();
            }
        };
        gameLoop.start();
    }

    private void startSpawningPowerUps() {
        spawnPowerUpsTimer = new AnimationTimer() {
            private long lastSpawnTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (now - lastSpawnTime >= 10_000_000_000L) { // 5 seconds in nanoseconds
                    spawnPowerUp();
                    lastSpawnTime = now;
                }
            }
        };
        spawnPowerUpsTimer.start();
    }

    private void startSpawningObstacles() {
        spawnObstaclesTimer = new AnimationTimer() {
            private long lastSpawnTime = System.nanoTime();
            private long lastIncrementTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (now - lastSpawnTime >= 1_000_000_000L) {
                    for (int i = 0; i < enemyCount; i++) {
                        spawnObstacle();
                    }
                    lastSpawnTime = now;
                }
                if (now - lastIncrementTime >= 15_000_000_000L) {
                    enemyCount++;  // Increase the number of enemies to spawn
                    enemySpeed = (double) (enemySpeed + 0.8);
                    lastIncrementTime = now;
                }
            }
        };
        spawnObstaclesTimer.start();
    }

    private void spawnPowerUp() {
        double x = random.nextDouble() * (root.getWidth() - 20);
        double y = random.nextDouble() * (root.getHeight() - 20);

        PowerUp powerUp = new PowerUp(x, y, 20, 20, Math.random() < 0.5 ? "speed" : "extraLife");
        root.getChildren().add(powerUp);
    }

    private void spawnObstacle() {
        double x = random.nextDouble() * (root.getWidth() - 30);
        double speed = random.nextDouble() * enemySpeed;
        Obstacle obstacle = new Obstacle(x, 0, 30, 30, speed); // Spawn at top (Y = 0)
        root.getChildren().add(obstacle);
    }

    private void moveObstacles() {
        List<Node> obstaclesToRemove = new ArrayList<>();
        for (Node node : root.getChildren()) {
            if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                obstacle.moveDown(); // Move obstacle downwards
                // Check if obstacle goes below the scene height, then remove
                if (obstacle.getY() > root.getHeight()) {
                    obstaclesToRemove.add(obstacle);
                }
            }
        }
        root.getChildren().removeAll(obstaclesToRemove);
    }

    // private void checkCollisions() {
    //     for (Node node : root.getChildren()) {
    //         if (node instanceof PowerUp) {
    //             PowerUp powerUp = (PowerUp) node;
    //             if (player.getBoundsInParent().intersects(powerUp.getBoundsInParent())) {
    //                 root.getChildren().remove(powerUp);
    //                 // handleCollision(player, powerUp); // Handle power-up collision
    //             }
    //         } else if (node instanceof Obstacle) {
    //             Obstacle obstacle = (Obstacle) node;
    //             if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
    //                 root.getChildren().remove(obstacle);
    //                 // handleCollision(player, obstacle); // Handle obstacle collision
    //             }
    //         }
    //     }
    // }

    private void checkCollisions() {
        List<Node> nodesToRemove = new ArrayList<>();

        for (Node node : root.getChildren()) {
            if (node instanceof PowerUp) {
                PowerUp powerUp = (PowerUp) node;
                if (player.getBoundsInParent().intersects(powerUp.getBoundsInParent())) {
                    powerUp.handleCollision(playerController, root); // Handle power-up collision
                    nodesToRemove.add(powerUp);
                }
            } else if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {

                    playerController.handleCollision(); // Handle obstacle collision (e.g., decrease player health)
                    nodesToRemove.add(obstacle);
                }
            }
        }

        // Remove collided nodes
        root.getChildren().removeAll(nodesToRemove);
    }

    public void gameOver() {
        if (spawnObstaclesTimer != null) spawnObstaclesTimer.stop();
        if (spawnPowerUpsTimer != null) spawnPowerUpsTimer.stop();
        if (gameLoop != null) gameLoop.stop();

        // Create a new stage for the game over dialog
        Stage gameOverStage = new Stage();
        gameOverStage.initModality(Modality.APPLICATION_MODAL);
        gameOverStage.initOwner(stage);
        gameOverStage.setTitle("Game Over");

        // Create layout for game over dialog
        VBox gameOverLayout = new VBox(20);
        gameOverLayout.setAlignment(Pos.CENTER);
        InputStream imageStream = getClass().getResourceAsStream("/images/title-bg.png");
        Image backgroundImage = new Image(imageStream);
        BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);
        gameOverLayout.setBackground(new Background(background));

        Text gameOverText = new Text("Game over!\n");
        gameOverText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Minecraft.ttf"), 40));
        gameOverText.setFill(Color.RED);

        Text replayButton = createClickableText("Reset Game", true, () -> {
            resetGame();
            gameOverStage.close();
        });
        Text exitButton = createClickableText("Exit", true, () -> {
            System.exit(0);
        });

        gameOverLayout.getChildren().addAll(gameOverText, replayButton, exitButton);
        Scene gameOverScene = new Scene(gameOverLayout, 400, 400);
        gameOverStage.setScene(gameOverScene);
        gameOverStage.showAndWait();
    }

    private Text createClickableText(String text, boolean clickable, Runnable onClick) {
        Text clickableText = new Text(text);
        clickableText.setFont(Font.loadFont(getClass().getResourceAsStream("/fonts/Minecraft.ttf"), 18));
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

    void resetGame() {
        System.out.println("Resetting game...");

        // Reset player position and health
        player.setX(200);
        player.setY(300);
        playerController.setHealth(3);

        enemyCount = 1;
        enemySpeed = 1;

        // Clear the root pane and add the player back
        root.getChildren().clear();
        root.getChildren().addAll(backgroundImageView1, backgroundImageView2, player);

        // Reset other game elements
        startSpawningObstacles();
        startSpawningPowerUps();
        startGameLoop();

        // Create or update lives text
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {

                livesText.setText("lives: " + playerController.getHealth());

            }
        };
        gameLoop.start();

        livesText = new Text();
        livesText.setFill(Color.WHITE);
        InputStream fontStream = getClass().getResourceAsStream("/fonts/Minecraft.ttf");
        Font livesFont = Font.loadFont(fontStream, 16);
        livesText.setFont(livesFont);
        livesText.setX(320);
        livesText.setY(20);
        root.getChildren().add(livesText);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

