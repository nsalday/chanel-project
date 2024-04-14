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

        scene.setOnKeyPressed(event -> {
            double x = player.getX();
            double y = player.getY();

            switch (event.getCode()) {
                case UP, W -> player.setY(y - 10);
                case DOWN, S -> player.setY(y + 10);
                case LEFT, A -> player.setX(x - 10); 
                case RIGHT, D -> player.setX(x + 10); 
                default -> {} 
            }
        });

        primaryStage.setTitle("Galaxy Dash");

        primaryStage.setScene(scene);

        primaryStage.show();

        scene.getRoot().requestFocus();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
