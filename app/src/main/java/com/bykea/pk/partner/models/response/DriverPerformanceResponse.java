package com.bykea.pk.partner.models.response;

public class DriverPerformanceResponse extends CommonResponse {


    private DriverPerformanceData data;

    public DriverPerformanceData getData() {
        return data;
    }

    public void setData(DriverPerformanceData data) {
        this.data = data;
    }
}
