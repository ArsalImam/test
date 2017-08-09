package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.SettingsData;

public class SettingsResponse extends CommonResponse {
    private SettingsData data;

    public SettingsData getData() {
        return data;
    }

    public void setData(SettingsData data) {
        this.data = data;
    }
}