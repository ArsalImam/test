package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.PilotData;

public class UpdateProfileResponse extends CommonResponse {

    private PilotData data;

    public PilotData getData() {
        return data;
    }

    public void setData(PilotData data) {
        this.data = data;
    }
}
