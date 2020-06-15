package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import static com.bykea.pk.partner.utils.Constants.NOT_AVAILABLE;

public class BatchBookingDropoff {
    @SerializedName("lat")
    @Expose
    private double lat;
    @SerializedName("lng")
    @Expose
    private double lng;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("gps_address")
    @Expose
    private String gpsAddress;
    @SerializedName("reroute_booking_id")
    @Expose
    private String rerouteBookingId;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getGpsAddress() {
        return gpsAddress == null ? NOT_AVAILABLE : gpsAddress;
    }

    public void setGpsAddress(String gpsAddress) {
        this.gpsAddress = gpsAddress;
    }

    public String getRerouteBookingId() {
        return rerouteBookingId;
    }

    public void setRerouteBookingId(String rerouteBookingId) {
        this.rerouteBookingId = rerouteBookingId;
    }
}