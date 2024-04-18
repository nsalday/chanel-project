import javafx.animation.AnimationTimer;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Iterator;

public class PlayerController {
    private Rectangle player;
    private AnimationTimer timer;
    private Pane gamePane; 
    private List<Bullet> bullets = new ArrayList<>();
    private boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
    private boolean canShoot = true;
    private long lastShotTime = 0; 
    private long shot_delay= 300;

    public PlayerController(Rectangle player, Pane gamePane, Scene scene) {
        this.player = player;
        this.gamePane = gamePane;
        setUpKeyHandling(scene);
        startAnimationTimer();
    }

    private void setUpKeyHandling(Scene scene) {
        scene.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case UP: case W:
                    upPressed = true;
                    break;
                case DOWN: case S:
                    downPressed = true;
                    break;
                case LEFT: case A:
                    leftPressed = true;
                    break;
                case RIGHT: case D:
                    rightPressed = true;
                    break;
                case SPACE:
                    spacePressed = true;
                    break;
            }
        });

        scene.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case UP: case W:
                    upPressed = false;
                    break;
                case DOWN: case S:
                    downPressed = false;
                    break;
                case LEFT: case A:
                    leftPressed = false;
                    break;
                case RIGHT: case D:
                    rightPressed = false;
                    break;
                case SPACE:
                    spacePressed = false;
                    break;
            }
        });
    }

    private void shoot(Rectangle shooter) {
        long currentTime = System.currentTimeMillis();
        if (canShoot && (currentTime - lastShotTime >= shot_delay)) {
            double startX = shooter.getX() + shooter.getWidth() / 2;
            double startY = shooter.getY() - 5;
            Bullet newBullet = new Bullet(startX, startY);
            bullets.add(newBullet);
            gamePane.getChildren().add(newBullet.getShape());
            lastShotTime = currentTime;
            canShoot = false;
            
            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    canShoot = true;
                }
            }, shot_delay);
        }
    }

    private void startAnimationTimer() {
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                updateBullets();
                updatePlayerPosition();
                if (spacePressed) {  
                    shoot(player);
                }
            }
        };
        timer.start();
    }

    private void updateBullets() {
        for (Iterator<Bullet> iterator = bullets.iterator(); iterator.hasNext(); ) {
            Bullet bullet = iterator.next();
            bullet.move();
            if (bullet.isOffScreen()) {
                gamePane.getChildren().remove(bullet.getShape());
                iterator.remove();
            }
        }
    }

    private void updatePlayerPosition() {
        if (upPressed) {
            player.setY(player.getY() - 2);
        }
        if (downPressed) {
            player.setY(player.getY() + 2);
        }
        if (leftPressed) {
            player.setX(player.getX() - 2);
        }
        if (rightPressed) {
            player.setX(player.getX() + 2);
        }
    }
}
