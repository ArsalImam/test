package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.CitiesData;

import java.util.ArrayList;

public class GetCitiesResponse extends CommonResponse {
    private ArrayList<CitiesData> data;

    public ArrayList<CitiesData> getData() {
        return data;
    }

    public void setData(ArrayList<CitiesData> data) {
        this.data = data;
    }
}
