package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.loadboard.LoadboardBookingDetailData;

import java.util.List;

/**
 * Data Model class for Accept Call APIs
 */

public class LoadboardBookingDetailResponse extends CommonResponse {

    LoadboardBookingDetailData data;

    public LoadboardBookingDetailData getData() {
        return data;
    }
}
