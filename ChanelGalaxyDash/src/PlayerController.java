import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

public class PlayerController extends Application {
    private Rectangle player;

    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();

        player = new Rectangle(200, 200, 30, 30); 

        root.getChildren().add(player);

        Scene scene = new Scene(root, 400, 400);

        scene.setOnKeyPressed(event -> {
            double x = player.getX();
            double y = player.getY();

            if ((event.getCode() == KeyCode.UP) || (event.getCode() == KeyCode.W)) {
                player.setY(y - 10);
            } else if ((event.getCode() == KeyCode.DOWN) || (event.getCode() == KeyCode.S)) {
                player.setY(y + 10);
            } else if ((event.getCode() == KeyCode.LEFT) || (event.getCode() == KeyCode.A)) {
                player.setX(x - 10);
            } else if ((event.getCode() == KeyCode.RIGHT) || (event.getCode() == KeyCode.D)) {
                player.setX(x + 10);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setTitle("Simple Player Controller");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
