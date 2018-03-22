package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.ZoneData;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class GetZonesResponse extends CommonResponse {
    @SerializedName("city")
    private String cityName;
    private ArrayList<ZoneData> data;

    public ArrayList<ZoneData> getData() {
        return data;
    }

    public void setData(ArrayList<ZoneData> data) {
        this.data = data;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
