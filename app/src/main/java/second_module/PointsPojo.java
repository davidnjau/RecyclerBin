package second_module;

public class PointsPojo {

    private int points;
    private int amount;

    public PointsPojo(int points, int amount) {
        this.points = points;
        this.amount = amount;
    }

    public PointsPojo() {
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
