package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.DriverStatsData;
import com.bykea.pk.partner.models.data.Performance;

import java.util.ArrayList;

public class DriverStatsResponse extends CommonResponse {

    private ArrayList<Performance> performance;

    private DriverStatsData data;


    public ArrayList<Performance> getPerformance() {
        return performance;
    }

    public void setPerformance(ArrayList<Performance> performance) {
        this.performance = performance;
    }

    public DriverStatsData getData() {
        return data;
    }

    public void setData(DriverStatsData data) {
        this.data = data;
    }
}