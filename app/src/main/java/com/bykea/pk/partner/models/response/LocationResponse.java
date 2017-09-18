package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.UpdatedLocation;

public class LocationResponse extends CommonResponse {

    private long timestampserver;
    private UpdatedLocation data;

    public UpdatedLocation getData() {
        return data;
    }

    public long getTimestampserver() {
        return timestampserver;
    }

}