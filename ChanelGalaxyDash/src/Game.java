import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class Game extends Application {
    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Rectangle player = new Rectangle(50, 50, 50, 50);
        root.getChildren().add(player);

        Scene scene = new Scene(root, 400, 400);
        PlayerController playerController = new PlayerController(player, root, scene); 

        primaryStage.setTitle("Galaxy Dash");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

