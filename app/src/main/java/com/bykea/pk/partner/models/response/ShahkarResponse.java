package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.ShahkarData;

import java.util.List;

public class ShahkarResponse extends CommonResponse {

    private List<ShahkarData> data;


    public List<ShahkarData> getData() {
        return data;
    }

    public void setData(List<ShahkarData> data) {
        this.data = data;
    }
}
