package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

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
