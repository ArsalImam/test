package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.ServiceTypeData;

import java.util.ArrayList;

public class ServiceTypeResponse extends CommonResponse {

    ArrayList<ServiceTypeData> data;

    public ArrayList<ServiceTypeData> getData() {
        return data;
    }

    public void setData(ArrayList<ServiceTypeData> data) {
        this.data = data;
    }
}
