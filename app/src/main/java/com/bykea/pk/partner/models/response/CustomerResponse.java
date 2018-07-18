package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class CustomerResponse {
    String _id;

    @SerializedName("full_name")
    String fullName;

    @SerializedName("phone")
    String mobileNumber;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }
}
