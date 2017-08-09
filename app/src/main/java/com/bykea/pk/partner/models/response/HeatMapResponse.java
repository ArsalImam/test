package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.HeatmapLatlng;

import java.util.ArrayList;

public class HeatMapResponse extends CommonResponse {


    ArrayList<HeatmapLatlng> data;

    public ArrayList<HeatmapLatlng> getData() {
        return data;
    }

    public void setData(ArrayList<HeatmapLatlng> data) {
        this.data = data;
    }
}
