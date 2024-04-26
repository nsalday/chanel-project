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

    public void handleCollision(Rectangle player, Pane root) {
        if (player.getBoundsInParent().intersects(this.getBoundsInParent())) {
            switch (type) {
                case "speed":
                     //
                    break;
                case "extraLife":
                    //
                    break;
            }
            //root.getChildren().remove(this); // Remove the power-up from the scene upon collision
        }
    }
}
