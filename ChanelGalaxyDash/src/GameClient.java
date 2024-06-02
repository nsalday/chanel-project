import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;
import javafx.application.Platform;
import javafx.scene.image.ImageView;

public class GameClient {
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private GameState gameState;
    private List<ImageView> players;
    private List<PlayerController> playerControllers;

    public void start() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());

            // Set the socket for each PlayerController
            for (PlayerController playerController : playerControllers) {
                playerController.setSocket(socket);
            }

            new Thread(() -> {
                try {
                    while (true) {
                        GameState updatedGameState = (GameState) in.readObject();
                        Platform.runLater(() -> updateGameState(updatedGameState));
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendGameState(GameState gameState) {
        if (out != null) {
            try {
                out.writeObject(gameState);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("ObjectOutputStream is null, cannot send game state");
        }
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

    public void initialize(List<ImageView> players, List<PlayerController> playerControllers) {
        this.players = players;
        this.playerControllers = playerControllers;
    }

    public Socket getSocket() {
        return socket;
    }

    public static void main(String[] args) {
        new GameClient().start();
    }
}
