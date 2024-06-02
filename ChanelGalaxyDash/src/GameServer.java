import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class GameServer {
    private static final int PORT = 12345;
    private List<ClientHandler> clients = new ArrayList<>();
    private GameState gameState;

    public static void main(String[] args) {
        new GameServer().start();
    }

    public void start() {
        gameState = new GameState(new ArrayList<>());
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server started on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                clients.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcastGameState() {
        for (ClientHandler client : clients) {
            client.sendGameState(gameState);
        }
    }

    private class ClientHandler implements Runnable {
        private Socket socket;
        private ObjectOutputStream out;
        private ObjectInputStream in;

        public ClientHandler(Socket socket) {
            this.socket = socket;
            try {
                out = new ObjectOutputStream(socket.getOutputStream());
                in = new ObjectInputStream(socket.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            try {
                while (true) {
                    GameState receivedGameState = (GameState) in.readObject();
                    synchronized (gameState) {
                        gameState = receivedGameState;
                        broadcastGameState();
                    }
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        public void sendGameState(GameState gameState) {
            try {
                out.writeObject(gameState);
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
