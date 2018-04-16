package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class SignUpCompleteResponse {
    @SerializedName("status")
    private int code;
    private String message;
    private String _id;

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

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }
}
