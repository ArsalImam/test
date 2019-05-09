package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class PilotStatusResponse extends CommonResponse {

    @SerializedName("data")
    private PilotStatusData pilotStatusData;

    public PilotStatusResponse(PilotStatusData pilotStatusData) {
        this.pilotStatusData = pilotStatusData;
    }

    public PilotStatusData getPilotStatusData() {
        return pilotStatusData;
    }

    public void setPilotStatusData(PilotStatusData pilotStatusData) {
        this.pilotStatusData = pilotStatusData;
    }
}
