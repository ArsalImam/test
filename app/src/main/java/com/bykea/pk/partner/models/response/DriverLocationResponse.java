package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.LocationData;
import com.google.gson.annotations.SerializedName;

/***
 * Driver location response model POJO class which would be used for Parsing API response.
 */
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

