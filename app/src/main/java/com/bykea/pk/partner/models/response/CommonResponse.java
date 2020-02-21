package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

/***
 * Generic response pojo class.
 */
public class CommonResponse {

    private boolean success = true;
    private String message;
    private int code;
    @SerializedName("subcode")
    private int subCode;
    @SerializedName("uuid")
    private String uuid;

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

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}