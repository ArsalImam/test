package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.RunningTripData;

public class CheckDriverStatusResponse extends CommonResponse {

    private RunningTripData data;


    public RunningTripData getData() {
        return data;
    }

    public void setData(RunningTripData data) {
        this.data = data;
    }
}