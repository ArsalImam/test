package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SignUpAddNumberResponse {
    @SerializedName("status")
    private int code;
    private String message;
    @SerializedName("id")
    private String _id;
    private boolean verification;

    private SignUpUserData data;

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

    public SignUpUserData getData() {
        return data;
    }

    public void setData(SignUpUserData data) {
        this.data = data;
    }


    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }
}
