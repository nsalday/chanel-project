import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Multiplayer {
    private VBox chatBox;
    private TextArea chatArea;
    private TextField chatInputField;
    private DatagramSocket clientSocket;
    private InetAddress serverAddress;
    private int serverPort = 12345;
    private boolean isHost = false;
    private Stage primaryStage;

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(20);
        layout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));

        Text titleLabel = new Text("Multiplayer");
        titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 36));
        titleLabel.setFill(Color.WHITE);

        Text hostText = createClickableText("Host Game", true, this::hostGame);
        hostText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        hostText.setFill(Color.LIGHTGRAY);

        Text joinText = createClickableText("Join Game", true, this::joinGame);
        joinText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        joinText.setFill(Color.LIGHTGRAY);

        Text exitText = createClickableText("Exit", true, this::exitGame);
        exitText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        exitText.setFill(Color.WHITE);

        layout.getChildren().addAll(titleLabel, hostText, joinText, exitText);
        Scene titleScene = new Scene(layout, 400, 400);

        primaryStage.setScene(titleScene);
        primaryStage.show();
    }

    private Text createClickableText(String text, boolean clickable, Runnable onClick) {
        Text clickableText = new Text(text);
        clickableText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
        clickableText.setFill(Color.LIGHTGRAY);

        if (clickable) {
            clickableText.setOnMouseClicked(e -> onClick.run());

            clickableText.setOnMouseEntered(e -> {
                clickableText.setFill(Color.WHITE);
                clickableText.setText("\u25B6 " + text);
            });

            clickableText.setOnMouseExited(e -> {
                clickableText.setFill(Color.LIGHTGRAY);
                clickableText.setText(text);
            });
        }

        return clickableText;
    }

    private void hostGame() {
        System.out.println("Hosting game...");
        isHost = true;

        // Start the chat server in a new thread
        new Thread(() -> {
            ChatServer server = new ChatServer();
            server.start(12345);
        }).start();

        // Open the lobby and chat screen
        startLobby();
    }

    private void joinGame() {
        System.out.println("Joining game...");
        isHost = false;
        // Open the lobby and chat screen
        startLobby();
    }

    private void startLobby() {
        // Initialize the client socket
        try {
            clientSocket = new DatagramSocket();
            serverAddress = InetAddress.getByName("localhost");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create the lobby layout
        VBox lobbyLayout = new VBox();
        lobbyLayout.setAlignment(Pos.TOP_CENTER);
        lobbyLayout.setSpacing(20);
        lobbyLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        lobbyLayout.setPadding(new Insets(10));

        Text lobbyTitle = new Text("Lobby");
        lobbyTitle.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 36));
        lobbyTitle.setFill(Color.WHITE);

        // Chat area and input field
        chatArea = new TextArea();
        chatArea.setEditable(false);
        chatArea.setPrefHeight(100);
        chatInputField = new TextField();
        chatInputField.setPromptText("Type a message...");
        chatInputField.setOnAction(event -> {
            String text = chatInputField.getText();
            sendMessage(text);
            chatArea.appendText("You: " + text + "\n");
            chatInputField.clear();
        });

        VBox chatBox = new VBox(chatArea, chatInputField);
        chatBox.setAlignment(Pos.BOTTOM_LEFT);
        chatBox.setSpacing(5);
        chatBox.setPrefWidth(300);
        chatBox.setPrefHeight(150);

        HBox bottomLayout = new HBox();
        bottomLayout.setAlignment(Pos.BOTTOM_LEFT);
        bottomLayout.getChildren().add(chatBox);
        bottomLayout.setPadding(new Insets(10));
        
        VBox mainLayout = new VBox();
        mainLayout.setAlignment(Pos.TOP_CENTER);
        mainLayout.setSpacing(20);
        mainLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(lobbyTitle);

        if (isHost) {
            Button startButton = new Button("Start Game");
            startButton.setOnAction(e -> {
                sendMessage("START_GAME");
                startGame();
            });
            mainLayout.getChildren().add(startButton);
        }
        else {
            //send message to server that player has joined
            sendMessage("Player has joined the game");

            // Display waiting text
            Text waitingText = new Text("Waiting for host to start the game...");
            waitingText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
            waitingText.setFill(Color.WHITE);
            mainLayout.getChildren().add(waitingText);
        }

        mainLayout.getChildren().add(bottomLayout);
        Scene lobbyScene = new Scene(mainLayout, 600, 400);
        primaryStage.setScene(lobbyScene);
        primaryStage.show();

        // Start the thread to receive messages
        new Thread(this::receiveMessages).start();
    }

    private void startGame() {
        Platform.runLater(() -> {
            Game game = new Game();
            Stage gameStage = new Stage();
            game.start(gameStage);
            primaryStage.close();

            // Close the chat client
            if (clientSocket != null && !clientSocket.isClosed()) {
                clientSocket.close();
            }
        });
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
                Platform.runLater(() -> {
                    if (receivedMessage.equals("START_GAME")) {
                        startGame();
                    } else {
                        chatArea.appendText("Server: " + receivedMessage + "\n");
                    }
                });
            } catch (IOException e) {
                System.out.println("Client socket closed.");
                break;
            }
        }
    }

    private void exitGame() {
        if (clientSocket != null && !clientSocket.isClosed()) {
            clientSocket.close();
        }
        System.exit(0);
    }
}
