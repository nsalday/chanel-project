import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.layout.Pane;


public class PowerUp extends Rectangle {
    private String type;

    public PowerUp(double x, double y, double width, double height, String type) {
        super(x, y, width, height);
        this.type = type;
        initVisuals();
    }

    private void initVisuals() {
        switch (type) {
            case "speed":
                this.setFill(Color.RED);
                break;
            case "extraLife":
                this.setFill(Color.GREEN);
                break;
            default:
                this.setFill(Color.GRAY);
                break;
        }
    }

    public String getType() {
        return type;
    }

    public void handleCollision(PlayerController playerController, Pane root) {
        Rectangle player = playerController.getPlayer();
        if (player.getBoundsInParent().intersects(player.getBoundsInParent())) {
            switch (type) {
                case "speed":
                playerController.increaseSpeed();
                    break;
                case "extraLife":
                    playerController.extraHealth(1);
                    break;
            }
        }
    }
}
