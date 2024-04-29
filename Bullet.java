package com.example.chanel;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

class Bullet {
    private Circle bulletShape;
    private double speed = 3; // Speed of the bullet

    public Bullet(double startX, double startY) {
        bulletShape = new Circle(startX, startY, 5);
        Image bulletImage = new Image(getClass().getResourceAsStream("/images/bullet2.png"));
        bulletShape.setFill(new ImagePattern(bulletImage));
    }

    public void move() {
        bulletShape.setCenterY(bulletShape.getCenterY() - speed);
    }

    public Circle getShape() {
        return bulletShape;
    }

    public boolean isOffScreen() {
        return bulletShape.getCenterY() < 0;
    }

    public boolean checkCollision(Pane gamePane) {
        for (Node child : gamePane.getChildren()) {
            if (child instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) child;
                if (bulletShape.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                    return true;
                }
            }
        }
        return false;
    }
}
