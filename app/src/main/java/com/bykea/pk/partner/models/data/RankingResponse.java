package com.bykea.pk.partner.models.data;

import com.bykea.pk.partner.models.response.CommonResponse;

import java.util.List;

public class RankingResponse extends CommonResponse {

    private RankingData data;

    public RankingData getData() {
        return data;
    }

    public void setData(RankingData data) {
        this.data = data;
    }
}
