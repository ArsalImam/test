package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.ZoneData;

import java.util.ArrayList;

public class ZoneAreaResponse extends CommonResponse {
    private long timeStamp;

    private ArrayList<ZoneData> data;

    public ArrayList<ZoneData> getData() {
        return data;
    }

    public void setData(ArrayList<ZoneData> data) {
        this.data = data;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
