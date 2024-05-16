import javafx.application.Application;
import javafx.stage.Stage;

public class GUI extends Application{
    @Override
    public void start(Stage primaryStage) {
        TitleScreen titleScreen = new TitleScreen();
        titleScreen.start();
    }
}
