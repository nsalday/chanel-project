// package com.example.chanel;

// public class App extends Application {
//     public static void main(String[] args) {
//         launch(args);
//     }

//     @Override
//     public void start(Stage primaryStage) {
//         TitleScreen titleScreen = new TitleScreen();
//         titleScreen.start();
//     }
// }

public class App {
    public static void main(String[] args) {
        GUI.launch(GUI.class, args);
    }
}