package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class VerifyNumberResponse extends CommonResponse {

    private String link;
    @SerializedName("support")
    private String supportNumber;

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSupportNumber() {
        return supportNumber;
    }

    public void setSupportNumber(String supportNumber) {
        this.supportNumber = supportNumber;
    }
}
