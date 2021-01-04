package com.bykea.pk.partner.models.response;


import com.google.gson.annotations.SerializedName;

/**
 * Model class for Places Details API response (we will use Geo Code API to get places details from v4.37 onwards)
 */
public class BykeaPlaceDetailsResponse extends CommonResponse {
    @SerializedName("data")
    private GeocoderApi data;

    public GeocoderApi getData() {
        return data;
    }

    public void setData(GeocoderApi data) {
        this.data = data;
    }
}