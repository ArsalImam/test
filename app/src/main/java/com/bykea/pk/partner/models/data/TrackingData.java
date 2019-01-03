package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Tracking Data
 */
public class TrackingData {

    private String lat;
    private String lng;
    private String date;

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
}
