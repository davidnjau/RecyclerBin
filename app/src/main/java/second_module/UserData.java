package second_module;

public class UserData {

    private String fullName;
    private String email;
    private String mobile;
    private String region;
    private String area;
    private String orderId;
    private String points;

    public UserData() {
    }

    public UserData(String fullName, String email, String mobile, String region, String area, String orderId, String points) {
        this.fullName = fullName;
        this.email = email;
        this.mobile = mobile;
        this.region = region;
        this.area = area;
        this.orderId = orderId;
        this.points = points;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }
}
