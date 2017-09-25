package com.bykea.pk.partner.models.data;

import com.bykea.pk.partner.models.response.CommonResponse;
import com.google.gson.annotations.SerializedName;

public class NotificationData extends CommonResponse {
    @SerializedName("available")
    private boolean isActive;

    public boolean isActive() {
        return isActive;
    }
}
