package com.bykea.pk.partner.models.response;

public class CheckDriverStatusResponse extends CommonResponse {

    private NormalCallData data;


    public NormalCallData getData() {
        return data;
    }

    public void setData(NormalCallData data) {
        this.data = data;
    }
}