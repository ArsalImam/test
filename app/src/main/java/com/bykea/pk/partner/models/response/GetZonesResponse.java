package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.ZoneData;

import java.util.ArrayList;

public class GetZonesResponse extends CommonResponse {

    private ArrayList<ZoneData> data;

    public ArrayList<ZoneData> getData() {
        return data;
    }

    public void setData(ArrayList<ZoneData> data) {
        this.data = data;
    }
}
