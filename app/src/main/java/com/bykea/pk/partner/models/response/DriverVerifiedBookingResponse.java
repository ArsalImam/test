package com.bykea.pk.partner.models.response;

public class DriverVerifiedBookingResponse extends CommonResponse {

    private DriverVerifiedBookingData data;

    public DriverVerifiedBookingData getData() {
        return data;
    }

    public void setData(DriverVerifiedBookingData data) {
        this.data = data;
    }
}
