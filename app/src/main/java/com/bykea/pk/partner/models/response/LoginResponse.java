package com.bykea.pk.partner.models.response;


import com.bykea.pk.partner.models.data.PilotData;

public class LoginResponse extends CommonResponse {
    private String link;
    private String support;
    private PilotData data;

    public PilotData getUser() {
        return data;
    }

    public void setUser(PilotData data) {
        this.data = data;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getSupport() {
        return support;
    }

    public void setSupport(String support) {
        this.support = support;
    }
}