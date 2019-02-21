package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData;

import java.util.List;

/**
 * Data Model class for Selected booking detail for loadboard
 */

public class LoadboardBookingDetailResponse extends CommonResponse {

    LoadboardBookingDetailData data;

    public LoadboardBookingDetailData getData() {
        return data;
    }
}
