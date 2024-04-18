import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

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
            case "shield":
                this.setFill(Color.BLUE);
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

    // // You can add methods here to activate the power-up effects
    // public void activate(Player player) {
    //     switch (type) {
    //         case "speed":
    //             player.increaseSpeed();
    //             break;
    //         case "shield":
    //             player.activateShield();
    //             break;
    //         case "extraLife":
    //             player.gainLife();
    //             break;
    //     }
    // }
}
