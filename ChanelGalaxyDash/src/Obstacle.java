// package com.example.chanel;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

import java.io.InputStream;
import java.util.Random;

public class Obstacle extends Rectangle {
    private double speed;
    private static final Random random = new Random();
    private static final String[] obstacleImagePaths = {
            "/resources/images/obstacle1.png",
            "/resources/images/obstacle2.png",
            "/resources/images/obstacle3.png",
    };

    public Obstacle(double x, double y, double width, double height, double speed) {
        super(x, y, width, height);
        this.speed = speed;
        setFillWithRandomImage();
    }

    private void setFillWithRandomImage() {
        try {
            int index = random.nextInt(obstacleImagePaths.length);
            String imagePath = obstacleImagePaths[index];

            InputStream inputStream = getClass().getResourceAsStream(imagePath);
            if (inputStream != null) {
                Image image = new Image(inputStream);
                this.setFill(new ImagePattern(image));
            } else {
                System.err.println("Failed to load image: " + imagePath);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void moveDown() {
        setY(getY() + speed);
    }
    // private void startSpawning() {
    //     spawnTimeline = new Timeline(
    //             new KeyFrame(Duration.seconds(5), event -> {
    //                 spawnNewObstacle();
    //             })
    //     );
    //     spawnTimeline.setCycleCount(Timeline.INDEFINITE);
    //     spawnTimeline.play();
    // }

    // private void spawnNewObstacle() {
    //     double x = random.nextDouble() * (root.getWidth() - getWidth());
    //     double y = random.nextDouble() * (root.getHeight() - getHeight());
    //     Obstacle newObstacle = new Obstacle(x, y, getWidth(), getHeight(), root);
    //     root.getChildren().add(newObstacle);
    // }

    public void handleCollision(Rectangle player, Pane root) {
        if (player.getBoundsInParent().intersects(this.getBoundsInParent())) {
            //root.getChildren().remove(this); // Remove the power-up from the scene upon collision
        }
    }
}