package com.bykea.pk.partner.models.response;

import com.bykea.pk.partner.models.data.SettingsData;
import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class SettingsResponse extends CommonResponse {
    private SettingsData data;
    @SerializedName("s_ver")
    private String setting_version;

    public SettingsData getData() {
        return data;
    }

    public void setData(SettingsData data) {
        this.data = data;
    }

    public String getSetting_version() {
        return StringUtils.isNotBlank(setting_version) ? setting_version : StringUtils.EMPTY;
    }

    public void setSetting_version(String setting_version) {
        this.setting_version = setting_version;
    }
}