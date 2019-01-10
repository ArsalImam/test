package com.bykea.pk.partner.models.request;

import com.google.gson.annotations.SerializedName;

/***
 * Driver Availability Request Model class which would be used to parse API Response
 */
public class DriverAvailabilityRequest {

    @SerializedName("is_available")
    private boolean isAvailable;

    @SerializedName("_id")
    private String id;

    @SerializedName("token_id")
    private String tokenID;

    @SerializedName("current_lat")
    private double latitude;

    @SerializedName("current_lng")
    private double longitude;

    @SerializedName("cih")
    private int cih;

    @SerializedName("imei")
    private String imei;

    @SerializedName("end_lat")
    private String endingLatitude;

    @SerializedName("end_lng")
    private String endingLongitude;

    @SerializedName("end_address")
    private String endingAddress;


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

    public String getEndingLatitude() {
        return endingLatitude;
    }

    public void setEndingLatitude(String endingLatitude) {
        this.endingLatitude = endingLatitude;
    }

    public String getEndingLongitude() {
        return endingLongitude;
    }

    public void setEndingLongitude(String endingLongitude) {
        this.endingLongitude = endingLongitude;
    }

    public String getEndingAddress() {
        return endingAddress;
    }

    public void setEndingAddress(String endingAddress) {
        this.endingAddress = endingAddress;
    }

    //endregion


}
