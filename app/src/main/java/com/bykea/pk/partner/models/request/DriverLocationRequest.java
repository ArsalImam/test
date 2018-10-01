package com.bykea.pk.partner.models.request;

import com.google.gson.annotations.SerializedName;

public class DriverLocationRequest {

    @SerializedName("driver_id")
    private String driverID;

    @SerializedName("token")
    private String token;

    @SerializedName("status")
    private String status;

    @SerializedName("trip_id")
    private String tripID;

    @SerializedName("in_call")
    private boolean inCall;

    @SerializedName("lat")
    private double latitude;

    @SerializedName("lng")
    private double longitude;


    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public boolean isInCall() {
        return inCall;
    }

    public void setInCall(boolean inCall) {
        this.inCall = inCall;
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
}
