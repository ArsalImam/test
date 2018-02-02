package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.NearByResults;

public class PlaceDetailsResponse {
    private NearByResults result;

    private String status;

    public NearByResults getResult() {
        return result;
    }

    public void setResult(NearByResults result) {
        this.result = result;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}