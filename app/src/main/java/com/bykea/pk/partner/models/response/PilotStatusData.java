package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

/**
 * Model class for Pilot status
 */
public class PilotStatusData {

    @SerializedName("cash")
    private boolean cashValue;


    public PilotStatusData(boolean cashValue) {
        this.cashValue = cashValue;
    }

    public boolean isCashValue() {
        return cashValue;
    }

    public void setCashValue(boolean cashValue) {
        this.cashValue = cashValue;
    }
}
