package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.LocationData;
import com.google.gson.annotations.SerializedName;

public class DriverLocationResponse extends CommonResponse {

    @SerializedName("data")
    private LocationData locationData;

    public LocationData getLocationData() {
        return locationData;
    }

    public void setLocationData(LocationData locationData) {
        this.locationData = locationData;
    }
}

