import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
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

    InputStream imageStream = getClass().getResourceAsStream("/resources/images/title-bg.png");
    Image backgroundImage = new Image(imageStream);
    BackgroundImage background = new BackgroundImage(backgroundImage, BackgroundRepeat.REPEAT, BackgroundRepeat.REPEAT, BackgroundPosition.DEFAULT, BackgroundSize.DEFAULT);

    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        VBox layout = new VBox();
        layout.setAlignment(Pos.CENTER);
        layout.setSpacing(20);

        layout.setBackground(new Background(background));

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(173, 31, 44, 1));

        Text titleLabel = new Text("Multiplayer");
        titleLabel.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Mario-Kart-DS.ttf"), 36));
        titleLabel.setFill(Color.RED);
        titleLabel.setEffect(dropShadow);

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
        VBox lobbyLayout = new VBox(20);
        lobbyLayout.setAlignment(Pos.CENTER);
        lobbyLayout.setSpacing(20);

        DropShadow dropShadow = new DropShadow();
        dropShadow.setRadius(5);
        dropShadow.setOffsetX(5);
        dropShadow.setOffsetY(5);
        dropShadow.setColor(Color.rgb(173, 31, 44, 1));

        lobbyLayout.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        lobbyLayout.setPadding(new Insets(10));

        Text lobbyTitle = new Text("Lobby");
        lobbyTitle.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Mario-Kart-DS.ttf"), 36));
        lobbyTitle.setFill(Color.RED);
        lobbyTitle.setEffect(dropShadow);

        // Chat area and input field
        chatArea = new TextArea();
        chatArea.setEditable(false);

        chatArea.setStyle(
                "-fx-control-inner-background: black;" +
                        "-fx-background-color: rgba(255, 255, 255, 0.1);" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;"
        );
        chatArea.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 12));

        chatInputField = new TextField();
        chatInputField.setPromptText("Type a message...");
        chatInputField.setStyle(
                "-fx-background-color: rgba(66, 66, 66, 1);" +
                        "-fx-border-color: white;" +
                        "-fx-border-width: 2px;" +
                        "-fx-text-fill: white"
        );

        chatInputField.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 12));

        chatInputField.setOnAction(event -> {
            String text = chatInputField.getText();
            sendMessage(text);
            chatArea.appendText("You: " + text + "\n");
            chatInputField.clear();
        });


        VBox chatBox = new VBox(chatArea, chatInputField);
        chatBox.setAlignment(Pos.CENTER);
        chatBox.setSpacing(5);
        chatBox.setPrefWidth(300);
        chatBox.setPrefHeight(200);

        HBox bottomLayout = new HBox();
        bottomLayout.setAlignment(Pos.CENTER);
        bottomLayout.getChildren().add(chatBox);
        bottomLayout.setPadding(new Insets(10));

        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);

        mainLayout.setSpacing(20);
        mainLayout.setBackground(new Background(background));
        mainLayout.setPadding(new Insets(10));
        mainLayout.getChildren().addAll(lobbyTitle);

        if (isHost) {
            Text startButton = createClickableText("Start Game", true, () -> {
                sendMessage("START_GAME");
                startGame();
            });
            startButton.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
            startButton.setFill(Color.LIGHTGRAY);
            mainLayout.getChildren().add(startButton);
        }
        else {
            //send message to server that player has joined
            sendMessage("Player has joined the game");

            // Display waiting text
            Text waitingText = new Text("Waiting for host to start the game...");
            waitingText.setFont(Font.loadFont(getClass().getResourceAsStream("/resources/fonts/Minecraft.ttf"), 18));
            waitingText.setFill(Color.LIGHTGRAY);
            mainLayout.getChildren().add(waitingText);
        }

        mainLayout.getChildren().add(bottomLayout);
        Scene lobbyScene = new Scene(mainLayout, 400, 400);
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
