import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Game extends Application {
    private Pane root;
    private Rectangle player;
    private Random random;

    @Override
    public void start(Stage primaryStage) {
        root = new Pane();
        player = new Rectangle(50, 50, 50, 50);
        root.getChildren().add(player);

        Scene scene = new Scene(root, 400, 400);

        // Handle player movement
        scene.setOnKeyPressed(event -> {
            double x = player.getX();
            double y = player.getY();

            switch (event.getCode()) {
                case UP, W -> player.setY(Math.max(y - 10, 0)); // Limit movement within bounds
                case DOWN, S -> player.setY(Math.min(y + 10, root.getHeight() - player.getHeight()));
                case LEFT, A -> player.setX(Math.max(x - 10, 0));
                case RIGHT, D -> player.setX(Math.min(x + 10, root.getWidth() - player.getWidth()));
                default -> {}
            }
        });

        primaryStage.setTitle("Galaxy Dash");
        primaryStage.setScene(scene);
        primaryStage.show();

        random = new Random();
        startSpawningPowerUps();
        startSpawningObstacles();

        // Game loop using AnimationTimer
        AnimationTimer gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollisions();
            }
        };
        gameLoop.start();
    }

    private void startSpawningPowerUps() {
        // Spawns a power-up every 5 seconds
        AnimationTimer timer = new AnimationTimer() {
            private long lastSpawnTime = 0;

            @Override
            public void handle(long now) {
                if (now - lastSpawnTime >= 5_000_000_000L) { // 5 seconds in nanoseconds
                    spawnPowerUp();
                    lastSpawnTime = now;
                }
            }
        };
        timer.start();
    }

    private void startSpawningObstacles() {
        // Spawns an obstacle every 3 seconds
        AnimationTimer timer = new AnimationTimer() {
            private long lastSpawnTime = 0;

            @Override
            public void handle(long now) {
                if (now - lastSpawnTime >= 3_000_000_000L) { // 3 seconds in nanoseconds
                    spawnObstacle();
                    lastSpawnTime = now;
                }
            }
        };
        timer.start();
    }

    private void spawnPowerUp() {
        double x = random.nextDouble() * (root.getWidth() - 20);
        double y = random.nextDouble() * (root.getHeight() - 20);

        PowerUp powerUp = new PowerUp(x, y, 20, 20, "speed");
        root.getChildren().add(powerUp);
    }

    private void spawnObstacle() {
        double x = random.nextDouble() * (root.getWidth() - 30);
        double y = random.nextDouble() * (root.getHeight() - 30);

        Obstacle obstacle = new Obstacle(x, y, 30, 30, root);
        root.getChildren().add(obstacle);
    }

    private void checkCollisions() {
        List<Node> nodesToRemove = new ArrayList<>();

        for (Node node : root.getChildren()) {
            if (node instanceof PowerUp) {
                PowerUp powerUp = (PowerUp) node;
                if (player.getBoundsInParent().intersects(powerUp.getBoundsInParent())) {
                    powerUp.handleCollision(player, root); // Handle power-up collision
                    nodesToRemove.add(powerUp);
                }
            } else if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {

                    obstacle.handleCollision(player, root); // Handle obstacle collision (e.g., decrease player health)
                    nodesToRemove.add(obstacle);
                }
            }
        }

        // Remove collided nodes
        root.getChildren().removeAll(nodesToRemove);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
