package com.bykea.pk.partner.models.request;

public class DriverAvailabilityRequest {

    @SuppressWarnings("is_available")
    private boolean isAvailable;

    @SuppressWarnings("_id")
    private String id;

    @SuppressWarnings("token_id")
    private String tokenID;

    @SuppressWarnings("lat")
    private double latitude;

    @SuppressWarnings("lng")
    private double longitude;

    @SuppressWarnings("cih")
    private int cih;

    @SuppressWarnings("imei")
    private String imei;


    //region Getter Setters
    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public int getCih() {
        return cih;
    }

    public void setCih(int cih) {
        this.cih = cih;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    //endregion


}
