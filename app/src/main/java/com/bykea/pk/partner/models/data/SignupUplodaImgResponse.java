package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class SignupUplodaImgResponse {
    @SerializedName("status")
    private int code;
    private String message;
    @SerializedName("image_type")
    private String type;

    @SerializedName("image")
    private String link;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
