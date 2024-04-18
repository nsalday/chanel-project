import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;

public class PlayerController {
    private Rectangle player;
    private AnimationTimer timer;
    private boolean upPressed, downPressed, leftPressed, rightPressed;

    public PlayerController(Rectangle player, Scene scene) {
        this.player = player;
        setUpKeyHandling(scene);
        startAnimationTimer();
    }

    private void setUpKeyHandling(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP:
                case W:
                    upPressed = true;
                    break;
                case DOWN:
                case S:
                    downPressed = true;
                    break;
                case LEFT:
                case A:
                    leftPressed = true;
                    break;
                case RIGHT:
                case D:
                    rightPressed = true;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP:
                case W:
                    upPressed = false;
                    break;
                case DOWN:
                case S:
                    downPressed = false;
                    break;
                case LEFT:
                case A:
                    leftPressed = false;
                    break;
                case RIGHT:
                case D:
                    rightPressed = false;
                    break;
            }
        });
    }

    private void startAnimationTimer() {
        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 16_666_667) { // Allows player to run in 60 FPS
                    updatePlayer();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }

    private void updatePlayer() {
        double dx = 0, dy = 0;
        if (upPressed) dy -= 5;
        if (downPressed) dy += 5;
        if (leftPressed) dx -= 5;
        if (rightPressed) dx += 5;
        movePlayer(dx, dy);
    }

    private void movePlayer(double dx, double dy) {
        player.setLayoutX(player.getLayoutX() + dx);
        player.setLayoutY(player.getLayoutY() + dy);
    }
}
