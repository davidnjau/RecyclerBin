package second_module;

public class BalancePojo {

    private String createdAt;
    private String points;

    public BalancePojo() {
    }

    public BalancePojo(String createdAt, String points) {
        this.createdAt = createdAt;
        this.points = points;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
