package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Tracking Data
 */
public class TrackingData {

    private String lat;
    private String lng;
    private String date;

    //For Batch
    @SerializedName("trip_id")
    private String tripID;

    @SerializedName("est_remaining_time")
    private int remainingTime;

    @SerializedName("est_remaining_distance")
    private double remainingDistance;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTripID() {
        return tripID;
    }

    public void setTripID(String tripID) {
        this.tripID = tripID;
    }

    public int getRemainingTime() {
        return remainingTime;
    }

    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    public double getRemainingDistance() {
        return remainingDistance;
    }

    public void setRemainingDistance(double remainingDistance) {
        this.remainingDistance = remainingDistance;
    }
}
