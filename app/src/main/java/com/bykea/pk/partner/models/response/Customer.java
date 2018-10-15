package com.bykea.pk.partner.models.response;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Customer Data Model class for Scheduled Delivery Service
 */
public class Customer implements Parcelable {

    @SerializedName("_id")
    private String id;

    @SerializedName("full_name")
    private String fullName;

    @SerializedName("phone")
    private String mobileNumber;

    //region Parcelable Helper methods

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.fullName);
        dest.writeString(this.mobileNumber);
    }

    public Customer() {
    }

    protected Customer(Parcel in) {
        this.id = in.readString();
        this.fullName = in.readString();
        this.mobileNumber = in.readString();
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel source) {
            return new Customer(source);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    //endregion

    //region Getter Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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


    //endregion
}
