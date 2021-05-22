package com.remo;

public class RecyclerBinPojo {

    private String recyclerBin;
    private String phoneNumber;
    private String latitude;
    private String longitude;
    private String openTime;
    private String closeTime;

    public RecyclerBinPojo(String recyclerBin, String phoneNumber, String latitude, String longitude, String openTime, String closeTime) {
        this.recyclerBin = recyclerBin;
        this.phoneNumber = phoneNumber;
        this.latitude = latitude;
        this.longitude = longitude;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public RecyclerBinPojo() {
    }

    public String getRecyclerBin() {
        return recyclerBin;
    }

    public void setRecyclerBin(String recyclerBin) {
        this.recyclerBin = recyclerBin;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }
}

