import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {
    private DatagramSocket serverSocket;
    private Set<ClientInfo> clients = new HashSet<>();

    private static class ClientInfo {
        InetAddress address;
        int port;

        ClientInfo(InetAddress address, int port) {
            this.address = address;
            this.port = port;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ClientInfo that = (ClientInfo) o;
            return port == that.port && address.equals(that.address);
        }

        @Override
        public int hashCode() {
            return 31 * address.hashCode() + port;
        }

        @Override
        public String toString() {
            return address.getHostAddress() + ":" + port;
        }
    }

    public void start(int port) {
        try {
            serverSocket = new DatagramSocket(port);
            System.out.println("Server started on port " + port);
            byte[] receiveBuffer = new byte[1024];

            while (true) {
                DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
                serverSocket.receive(receivePacket);
                String message = new String(receivePacket.getData(), 0, receivePacket.getLength());
                System.out.println("Received message: " + message);

                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                ClientInfo clientInfo = new ClientInfo(clientAddress, clientPort);

                if (!clients.contains(clientInfo)) {
                    clients.add(clientInfo);
                    System.out.println("Added client: " + clientInfo);
                }

                if (message.equals("DIE")) {
                    handlePlayerDeath(clientInfo);
                } else if (message.equals("START_GAME")) {
                    broadcast("START_GAME", null);  // Broadcast the start game signal to all clients
                } else {
                    // Broadcast message excluding the sender
                    broadcast(message, clientInfo);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void handlePlayerDeath(ClientInfo clientInfo) {
        System.out.println("Initial client count: " + (clients.size()-1));
        boolean removed = clients.remove(clientInfo);
        System.out.println("Removed client: " + clientInfo + " Success: " + removed);
        System.out.println("Player " + clientInfo + " died. " + (clients.size()-1) + " players remaining.");
        broadcast("A player has died. " + (clients.size()-1) + " players remaining.", null);

        if (clients.size() == 2) {
            ClientInfo winner = clients.iterator().next();
            System.out.println("Player " + winner + " wins!");
            broadcast("YOU_WIN", winner);
            broadcast("GAME_OVER", null);
            stop();
        }
    }

    private void broadcast(String message, ClientInfo excludeClient) {
        for (ClientInfo client : clients) {
            if (excludeClient == null || !client.equals(excludeClient)) {
                try {
                    sendResponse(message, client.address, client.port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void sendResponse(String response, InetAddress clientAddress, int clientPort) throws IOException {
        byte[] sendData = response.getBytes();
        DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, clientAddress, clientPort);
        serverSocket.send(sendPacket);
    }

    public void stop() {
        if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            System.out.println("Server stopped.");
        }
    }

    public static void main(String[] args) {
        ChatServer server = new ChatServer();
        server.start(12345);
    }
}
