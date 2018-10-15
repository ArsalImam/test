package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.bykea.pk.partner.models.response.Customer;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Data Model class for Scheduled Delivery Service
 */

public class DeliveryScheduleModel implements Parcelable {

    @SerializedName("_id")
    private String id;

    @SerializedName("refId")
    private String refId;

    @SerializedName("dt")
    private String dateTime;

    @SerializedName("booking_no")
    private String bookingNo;

    @SerializedName("distance")
    private String distance;

    @SerializedName("customer")
    private Customer customer;

    @SerializedName("loc")
    private ArrayList<String> latlng;

    @SerializedName("pickup_address")
    private String address;

    private String duration;


    public DeliveryScheduleModel() {
    }


    //region Parcelable Helper methods


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.refId);
        dest.writeString(this.dateTime);
        dest.writeString(this.bookingNo);
        dest.writeString(this.distance);
        dest.writeParcelable(this.customer, flags);
        dest.writeStringList(this.latlng);
        dest.writeString(this.address);
        dest.writeString(this.duration);
    }

    protected DeliveryScheduleModel(Parcel in) {
        this.id = in.readString();
        this.refId = in.readString();
        this.dateTime = in.readString();
        this.bookingNo = in.readString();
        this.distance = in.readString();
        this.customer = in.readParcelable(Customer.class.getClassLoader());
        this.latlng = in.createStringArrayList();
        this.address = in.readString();
        this.duration = in.readString();
    }

    public static final Creator<DeliveryScheduleModel> CREATOR = new Creator<DeliveryScheduleModel>() {
        @Override
        public DeliveryScheduleModel createFromParcel(Parcel source) {
            return new DeliveryScheduleModel(source);
        }

        @Override
        public DeliveryScheduleModel[] newArray(int size) {
            return new DeliveryScheduleModel[size];
        }
    };

    //endregion

    //region Getter Setter

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getBookingNo() {
        return bookingNo;
    }

    public void setBookingNo(String bookingNo) {
        this.bookingNo = bookingNo;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public ArrayList<String> getLatlng() {
        return latlng;
    }

    public void setLatlng(ArrayList<String> latlng) {
        this.latlng = latlng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }


    //endregion


}
