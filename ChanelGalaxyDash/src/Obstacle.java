import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;

import java.util.Random;

public class Obstacle extends Rectangle {
    private Timeline spawnTimeline;
    private Pane root;
    private Random random;

    public Obstacle(double x, double y, double width, double height, Pane root) {
        super(x, y, width, height);
        setFill(Color.GRAY); // Customize obstacle appearance (e.g., color)
        this.root = root;
        this.random = new Random();
        // startSpawning();
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
