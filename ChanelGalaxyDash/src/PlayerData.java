import java.io.Serializable;

public class PlayerData implements Serializable {
    private double x;
    private double y;
    private long health;

    public PlayerData(double x, double y, long health) {
        this.x = x;
        this.y = y;
        this.health = health;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public long getHealth() {
        return health;
    }

    public void setHealth(long health) {
        this.health = health;
    }
}
