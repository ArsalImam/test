package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class BiometricApiResponse {
    @SerializedName("status")
    private int code;
    private String message;


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
}
