package com.bykea.pk.partner.models.data.loadboard;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadboardBookingLocationData {
    @SerializedName("lat")
    private double latitude;
    @SerializedName("lng")
    private double longitude;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
