package com.bykea.pk.partner.models.request;

import com.bykea.pk.partner.models.data.MultipleDeliveryRemainingETA;
import com.bykea.pk.partner.models.data.TrackingData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/***
 *  Driver Location Request Body model which would be used to send Request Parameters to API Server
 */
public class DriverLocationRequest {

    @SerializedName("token_id")
    private String tokenID;

    @SerializedName("_id")
    private String driverID;

    @SerializedName("lat")
    private String latitude;

    @SerializedName("lng")
    private String longitude;

    @SerializedName("status")
    private String availableStatus;

    @SerializedName("eta")
    private String eta;

    @SerializedName("distance")
    private String distance;

    @SerializedName("trip_id")
    private String tripID;

    @SerializedName("track")
    private ArrayList<TrackingData> trackingData;

    @SerializedName(("bookings"))
    private List<MultipleDeliveryRemainingETA> batchBookings;

    @SerializedName("uuid")
    private String uuid;

    //region Getter Setter

    public String getTokenID() {
        return tokenID;
    }

    public void setTokenID(String tokenID) {
        this.tokenID = tokenID;
    }

    public String getDriverID() {
        return driverID;
    }

    public void setDriverID(String driverID) {
        this.driverID = driverID;
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

    public String getAvailableStatus() {
        return availableStatus;
    }

    public void setAvailableStatus(String availableStatus) {
        this.availableStatus = availableStatus;
    }

    public String getEta() {
        return eta;
    }

    public void setEta(String eta) {
        this.eta = eta;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public ArrayList<TrackingData> getTrackingData() {
        return trackingData;
    }

    public void setTrackingData(ArrayList<TrackingData> trackingData) {
        this.trackingData = trackingData;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public List<MultipleDeliveryRemainingETA> getBatchBookings() {
        return batchBookings;
    }

    public void setBatchBookings(List<MultipleDeliveryRemainingETA> batchBookings) {
        this.batchBookings = batchBookings;
    }

    //endregion
}
