package com.example.chanel;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

class Bullet {
    private Circle bulletShape;
    private double speed = 5; // Speed of the bullet

    public Bullet(double startX, double startY) {
        bulletShape = new Circle(startX, startY, 5);
        bulletShape.setFill(Color.RED); 
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
}
