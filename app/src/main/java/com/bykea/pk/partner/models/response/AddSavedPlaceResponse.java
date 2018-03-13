package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class AddSavedPlaceResponse extends CommonResponse {

    @SerializedName("data")
    private String placeId;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
}
