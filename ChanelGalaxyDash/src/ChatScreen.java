import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ChatScreen extends Application {
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort = 12345;
    private TextArea textArea;

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("In-Game Chat");

        clientSocket = new DatagramSocket();
        serverAddress = InetAddress.getByName("localhost");

        textArea = new TextArea();
        TextField textField = new TextField();

        textField.setOnAction(event -> {
            String text = textField.getText();
            sendMessage(text);
            textArea.appendText("You: " + text + "\n");
            textField.clear();
        });

        VBox vbox = new VBox(textArea, textField);
        Scene scene = new Scene(vbox, 400, 300);
        primaryStage.setScene(scene);
        primaryStage.show();

        // Thread to receive messages
        Thread receiveThread = new Thread(this::receiveMessages);
        receiveThread.start();
    }

    private void sendMessage(String message) {
        try {
            byte[] sendData = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, serverAddress, serverPort);
            clientSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void receiveMessages() {
        byte[] receiveBuffer = new byte[1024];
        while (true) {
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            try {
                clientSocket.receive(receivePacket);
                String receivedMessage = new String(receivePacket.getData(), 0, receivePacket.getLength());
                javafx.application.Platform.runLater(() ->
                        textArea.appendText("Server: " + receivedMessage + "\n"));
            } catch (IOException e) {
                System.out.println("Client socket closed.");
                break;
            }
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        clientSocket.close();
    }
}

