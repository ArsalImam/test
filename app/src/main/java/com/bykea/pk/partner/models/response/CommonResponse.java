package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class CommonResponse {

    private boolean success;
    private String message;
    private int code;
    @SerializedName("subcode")
    private int subCode;

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public int getSubCode() {
        return subCode;
    }

    public void setSubCode(int subCode) {
        this.subCode = subCode;
    }
}