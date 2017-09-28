package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class CancelRideResponse extends CommonResponse {

    @SerializedName("is_available")
    private String available;

    public boolean isAvailable() {
        return !StringUtils.isNotBlank(available) || Boolean.parseBoolean(available);
    }
}