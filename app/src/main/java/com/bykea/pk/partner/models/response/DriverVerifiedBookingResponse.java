package com.bykea.pk.partner.models.response;

/**
 * model class for booking history stats api
 *
 * @author ArsalImam
 */
public class DriverVerifiedBookingResponse extends CommonResponse {

    private DriverVerifiedBookingData data;

    public DriverVerifiedBookingData getData() {
        return data;
    }

    public void setData(DriverVerifiedBookingData data) {
        this.data = data;
    }
}
