package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.DriverStatsData;

public class DriverStatsResponse extends CommonResponse {
   private DriverStatsData data;

    public DriverStatsData getData() {
        return data;
    }

    public void setData(DriverStatsData data) {
        this.data = data;
    }
}