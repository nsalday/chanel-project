import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Game extends Application {

    private Pane root;
    private List<ImageView> players;
    private ImageView backgroundImageView1;
    private ImageView backgroundImageView2;
    private Random random;
    private List<PlayerController> playerControllers;
    private AnimationTimer spawnObstaclesTimer;
    private AnimationTimer spawnPowerUpsTimer;
    private AnimationTimer gameLoop;
    private int enemyCount = 1;
    private double enemySpeed = 1;
    private double backgroundPosY = 0;
    private List<Text> livesTexts;
    private Stage stage;
    private ExecutorService executorService;

    // Networking fields
    private GameState gameState;
    private GameClient gameClient;

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;
        this.executorService = Executors.newCachedThreadPool();

        root = new Pane();
        players = new ArrayList<>();
        playerControllers = new ArrayList<>();
        livesTexts = new ArrayList<>();

        Image playerImage = new Image(getClass().getResourceAsStream("/player.png"));
        for (int i = 0; i < 4; i++) {
            ImageView player = new ImageView(playerImage);
            players.add(player);
        }

        Image backgroundImage = new Image(getClass().getResourceAsStream("/space_bg9.png"));
        backgroundImageView1 = new ImageView(backgroundImage);
        backgroundImageView2 = new ImageView(backgroundImage);
        backgroundImageView1.setFitWidth(800);
        backgroundImageView1.setFitHeight(800);
        backgroundImageView2.setFitWidth(800);
        backgroundImageView2.setFitHeight(800);

        root.getChildren().addAll(backgroundImageView1, backgroundImageView2);
        root.getChildren().addAll(players);

        Scene scene = new Scene(root, 800, 800);

        Menu menu = new Menu(primaryStage, scene);

        gameState = new GameState(new ArrayList<>());
        for (int i = 0; i < 4; i++) {
            gameState.getPlayers().add(new PlayerData(100 + (i * 150), 300, 3));
            PlayerController playerController = new PlayerController(players.get(i), root, scene, 3, this, menu, null, gameState, i); // Socket will be set by GameClient
            playerControllers.add(playerController);
            Text livesText = new Text();
            livesText.setFill(Color.WHITE);
            InputStream fontStream = getClass().getResourceAsStream("/Minecraft.ttf");
            Font livesFont = Font.loadFont(fontStream, 16);
            livesText.setFont(livesFont);
            livesText.setX(scene.getWidth() - 80);
            livesText.setY(20 + (i * 20));
            livesTexts.add(livesText);
            root.getChildren().add(livesText);
        }

        for (int i = 0; i < players.size(); i++) {
            ImageView player = players.get(i);
            player.setX(100 + (i * 150));
            player.setY(300);
        }

        primaryStage.setTitle("Galaxy Dash Multiplayer");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Initialize networking
        initializeNetworking();
    }

    private void initializeNetworking() {
        gameClient = new GameClient();
        gameClient.initialize(players, playerControllers);
        gameClient.start();

        // Ensure the game loop starts after the GameClient has connected
        executorService.execute(() -> {
            while (gameClient.getSocket() == null) {
                try {
                    Thread.sleep(100); // Wait for the GameClient to establish the connection
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            Platform.runLater(() -> {
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
                        for (int i = 0; i < playerControllers.size(); i++) {
                            livesTexts.get(i).setText("Lives: " + playerControllers.get(i).getHealth());
                        }
                    }
                };
                gameLoop.start();

                random = new Random();
                startSpawningPowerUps();
                startSpawningObstacles();
                startGameLoop();
            });
        });
    }

    private void updateGameState(GameState updatedGameState) {
        this.gameState = updatedGameState;
        List<PlayerData> playerDataList = gameState.getPlayers();
        for (int i = 0; i < playerDataList.size(); i++) {
            PlayerData playerData = playerDataList.get(i);
            ImageView playerImageView = players.get(i);
            playerImageView.setX(playerData.getX());
            playerImageView.setY(playerData.getY());
        }
    }

    private void startGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                checkCollisions();
                moveObstacles();
                sendGameStateToServer();
            }
        };
        gameLoop.start();
    }

    private void sendGameStateToServer() {
        gameClient.sendGameState(gameState);
    }

    private void startSpawningPowerUps() {
        spawnPowerUpsTimer = new AnimationTimer() {
            private long lastSpawnTime = System.nanoTime();

            @Override
            public void handle(long now) {
                if (now - lastSpawnTime >= 10_000_000_000L) { // 10 seconds in nanoseconds
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

    private void checkCollisions() {
        List<Node> nodesToRemove = new ArrayList<>();

        for (Node node : root.getChildren()) {
            if (node instanceof PowerUp) {
                PowerUp powerUp = (PowerUp) node;
                for (PlayerController playerController : playerControllers) {
                    if (players.get(playerControllers.indexOf(playerController)).getBoundsInParent().intersects(powerUp.getBoundsInParent())) {
                        powerUp.handleCollision(playerController, root); // Handle power-up collision
                        nodesToRemove.add(powerUp);
                    }
                }
            } else if (node instanceof Obstacle) {
                Obstacle obstacle = (Obstacle) node;
                for (PlayerController playerController : playerControllers) {
                    if (players.get(playerControllers.indexOf(playerController)).getBoundsInParent().intersects(obstacle.getBoundsInParent())) {
                        playerController.handleCollision(); // Handle obstacle collision (e.g., decrease player health)
                        nodesToRemove.add(obstacle);
                    }
                }
            }
        }

        // Remove collided nodes
        root.getChildren().removeAll(nodesToRemove);

        // Check if any player has lost all lives
        for (PlayerController playerController : playerControllers) {
            if (playerController.getHealth() <= 0) {
                gameOver(playerController);
            }
        }
    }

    public void gameOver(PlayerController playerController) {
        root.getChildren().remove(players.get(playerControllers.indexOf(playerController)));
        playerControllers.remove(playerController);
        if (playerControllers.size() == 1) {
            displayWinningScreen(playerControllers.get(0));
        }
    }

    private void displayWinningScreen(PlayerController winner) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Player " + (playerControllers.indexOf(winner) + 1) + " wins!", ButtonType.OK);
        alert.setTitle("Game Over");
        alert.setHeaderText("Congratulations!");
        alert.showAndWait();
        resetGame();
    }

    void resetGame() {
        System.out.println("Resetting game...");

        // Reset player positions and health
        playerControllers.clear();
        for (int i = 0; i < players.size(); i++) {
            ImageView player = players.get(i);
            player.setX(100 + (i * 150));
            player.setY(300);
            PlayerController playerController = new PlayerController(player, root, stage.getScene(), 3, this, new Menu(stage, stage.getScene()), null, gameState, i); // Socket will be set by GameClient
            playerControllers.add(playerController);
        }

        enemyCount = 1;
        enemySpeed = 1;

        // Clear the root pane and add the players back
        root.getChildren().clear();
        root.getChildren().addAll(backgroundImageView1, backgroundImageView2);
        root.getChildren().addAll(players);

        // Reset other game elements
        startSpawningObstacles();
        startSpawningPowerUps();
        startGameLoop();

        livesTexts.clear();
        for (int i = 0; i < playerControllers.size(); i++) {
            Text livesText = new Text();
            livesText.setFill(Color.WHITE);
            InputStream fontStream = getClass().getResourceAsStream("/Minecraft.ttf");
            Font livesFont = Font.loadFont(fontStream, 16);
            livesText.setFont(livesFont);
            livesText.setX(stage.getScene().getWidth() - 80);
            livesText.setY(20 + (i * 20));
            livesTexts.add(livesText);
            root.getChildren().add(livesText);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
