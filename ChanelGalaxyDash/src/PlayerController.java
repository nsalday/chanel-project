import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.input.KeyCode;
import javafx.scene.shape.Rectangle;
import javafx.scene.Node;
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
    private long show_delay_normal = 300;
    private long health;
    private Game game;
    private double normalSpeed = 2.0; 
    private double speed = 2.0; 

    public PlayerController(Rectangle player, Pane gamePane, Scene scene, int health, Game game) {
        this.player = player;
        this.gamePane = gamePane;
        this.health = health;
        this.game = game;
        this.speed = speed;
        this.shot_delay = shot_delay;
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
        if (canShoot && (currentTime - lastShotTime >= this.shot_delay)) {
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
            }, this.shot_delay);
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
        Iterator<Bullet> iterator = bullets.iterator();
        while (iterator.hasNext()) {
            Bullet bullet = iterator.next();
            bullet.move();
            if (bullet.isOffScreen()) {
                gamePane.getChildren().remove(bullet.getShape());
                iterator.remove();
            } else {
                // Check for collision with obstacles
                if (bullet.checkCollision(gamePane)) {
                    // Find and remove the colliding obstacle
                    for (Node child : gamePane.getChildren()) {
                        if (child instanceof Obstacle && bullet.getShape().getBoundsInParent().intersects(child.getBoundsInParent())) {
                            gamePane.getChildren().remove(child);
                            break; // Assuming one bullet can only hit one obstacle at a time
                        }
                    }
                    // Remove the bullet after collision
                    gamePane.getChildren().remove(bullet.getShape());
                    iterator.remove();
                }
            }
        }
    }

    private void updatePlayerPosition() {
        double playerNewX = player.getX();
        double playerNewY = player.getY();

        if (upPressed) {
            playerNewY -= this.speed;
        }
        if (downPressed) {
            playerNewY += this.speed;
        }
        if (leftPressed) {
            playerNewX -= this.speed;
        }
        if (rightPressed) {
            playerNewX += this.speed;
        }
        
        // Check boundaries and adjust if necessary
        double playerMaxX = gamePane.getWidth() - player.getWidth();
        double playerMaxY = gamePane.getHeight() - player.getHeight();
        if (playerNewX < 0) playerNewX = 0;
        if (playerNewX > playerMaxX) playerNewX = playerMaxX;
        if (playerNewY < 0) playerNewY = 0;
        if (playerNewY > playerMaxY) playerNewY = playerMaxY;

        // Set the new position
        player.setX(playerNewX);
        player.setY(playerNewY);
    }

    public void handleCollision() {
        // Check each obstacle for collision with the player
        for (Node child : gamePane.getChildren()) {
            if (child instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) child;
                if (player.getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                    this.health--; // Decrease health by one
                    System.out.println(this.health);
                    if(this.health == 0){
                        Platform.runLater(() -> game.gameOver());
                        break;
                    }
                }
            }
        }
    }

    public void setHealth(int newHealth) {
        health = newHealth;
    }

    public void extraHealth(int newHealth) {
        health = health + newHealth;
    }

    public Rectangle getPlayer() {
        return this.player;
    }

    public void increaseSpeed() {
        this.speed *= 1.5; // Increase speed by 50%
        this.shot_delay = 100;
        
        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                speed = normalSpeed; // Reset speed to normal after 5 seconds
                shot_delay = show_delay_normal;
            }
        },3000); // 5000 milliseconds = 5 seconds
    }
}
