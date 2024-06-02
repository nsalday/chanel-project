// package com.example.chanel;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public class PowerUp extends Rectangle {
    private String type;

    public PowerUp(double x, double y, double width, double height, String type) {
        super(x, y, width, height);
        this.type = type;
        initVisuals();
    }

    private void initVisuals() {
        switch (type) {
            case "speed":
                setFillWithImage("/speedup.png");
                break;
            case "extraLife":
                setFillWithImage("/heart.png");
                break;
            default:
                this.setFill(Color.GRAY);
                break;
        }
    }

    private void setFillWithImage(String imagePath) {
        try {
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


    public String getType() {
        return type;
    }

    public void handleCollision(PlayerController playerController, Pane root) {
        ImageView player = playerController.getPlayerImageView();
        if (player.getBoundsInParent().intersects(this.getBoundsInParent())) {
            switch (type) {
                case "speed":
                    playerController.increaseSpeed();
                    break;
                case "extraLife":
                    playerController.extraHealth(1);
                    break;
            }
        }
    }
}
