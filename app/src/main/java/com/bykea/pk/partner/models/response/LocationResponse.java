package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.UpdatedLocation;
import com.google.gson.annotations.SerializedName;

public class LocationResponse extends CommonResponse {

    @SerializedName("timestampserver")
    private long timeStampServer;

    @SerializedName("data")
    private UpdatedLocation location;

    public long getTimeStampServer() {
        return timeStampServer;
    }

    public void setTimeStampServer(long timeStampServer) {
        this.timeStampServer = timeStampServer;
    }

    public UpdatedLocation getLocation() {
        return location;
    }

    public void setLocation(UpdatedLocation location) {
        this.location = location;
    }
}