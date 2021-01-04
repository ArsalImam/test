package com.bykea.pk.partner.models;

import com.bykea.pk.partner.models.data.PlacesAutoCompleteData;
import com.bykea.pk.partner.models.response.CommonResponse;

/**
 * Model class for Bykea Places API response
 */
public class PlaceAutoCompleteResponse extends CommonResponse {

    private PlacesAutoCompleteData data;

    public PlacesAutoCompleteData getData() {
        return data;
    }

    public void setData(PlacesAutoCompleteData data) {
        this.data = data;
    }
}