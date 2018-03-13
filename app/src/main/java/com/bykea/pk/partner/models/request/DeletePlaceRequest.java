package com.bykea.pk.partner.models.request;

import com.google.gson.annotations.SerializedName;

public class DeletePlaceRequest {
    @SerializedName("id")
    private String placeId;

    @SerializedName("_id")
    private String userId;
    private String token_id;

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }
}
