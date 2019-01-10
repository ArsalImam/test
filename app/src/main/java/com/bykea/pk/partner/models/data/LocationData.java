package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/***
 * Location Model for holding server date time when driver location was received.
 */
public class LocationData {

    @SerializedName("dt")
    private String serverTime;

    public String getServerTime() {
        return serverTime;
    }

    public void setServerTime(String serverTime) {
        this.serverTime = serverTime;
    }
}
