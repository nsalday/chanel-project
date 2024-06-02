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

                // Register client
                InetAddress clientAddress = receivePacket.getAddress();
                int clientPort = receivePacket.getPort();
                clients.add(new ClientInfo(clientAddress, clientPort));

                if (message.equals("DIE")) {
                    handlePlayerDeath(clientAddress, clientPort);
                } else if (message.equals("START_GAME")) {
                    broadcast("START_GAME", null, 0);  // Broadcast the start game signal to all clients
                } else {
                    // Broadcast message excluding the sender
                    broadcast(message, clientAddress, clientPort);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            stop();
        }
    }

    private void handlePlayerDeath(InetAddress clientAddress, int clientPort) {
        clients.remove(new ClientInfo(clientAddress, clientPort));
        System.out.println("Player at " + clientAddress.getHostAddress() + ":" + clientPort + " died. " + clients.size() + " players remaining.");
        broadcast("A player has died. " + clients.size() + " players remaining.", null, 0);

        if (clients.size() == 1) {
            ClientInfo winner = clients.iterator().next();
            System.out.println("Player at " + winner.address.getHostAddress() + ":" + winner.port + " wins!");
            broadcast("Player at " + winner.address.getHostAddress() + ":" + winner.port + " wins!", null, 0);
            try {
                sendResponse("YOU_WIN", winner.address, winner.port);
            } catch (IOException e) {
                e.printStackTrace();
            }
            stop();
        }
    }

    private void broadcast(String message, InetAddress excludeAddress, int excludePort) {
        for (ClientInfo client : clients) {
            if (excludeAddress == null || !client.address.equals(excludeAddress) || client.port != excludePort) {
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
