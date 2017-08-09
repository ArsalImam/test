package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.UpdatedLocation;

public class LocationResponse extends CommonResponse {

    private long timestampserver;
    private UpdatedLocation data;

    public UpdatedLocation getData() {
        return data;
    }

    public void setData(UpdatedLocation data) {
        this.data = data;
    }

    public long getTimestampserver() {
        return timestampserver;
    }

    public void setTimestampserver(long timestampserver) {
        this.timestampserver = timestampserver;
    }
}