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

    private String _id;
    private String ref_id;
    @SerializedName("dt")
    private String dateTime;
    private String booking_no;
    private String distance;
    private Customer customer;
    @SerializedName("loc")
    private ArrayList<String> latlng;

    @SerializedName("pickup_address")
    private String address;
    private String duration;

    protected DeliveryScheduleModel(Parcel in) {
        _id = in.readString();
        ref_id = in.readString();
        dateTime = in.readString();
        booking_no = in.readString();
        distance = in.readString();
        customer = in.readParcelable(Customer.class.getClassLoader());
        latlng = in.createStringArrayList();
        address = in.readString();
        duration = in.readString();
    }

    public static final Creator<DeliveryScheduleModel> CREATOR = new Creator<DeliveryScheduleModel>() {
        @Override
        public DeliveryScheduleModel createFromParcel(Parcel in) {
            return new DeliveryScheduleModel(in);
        }

        @Override
        public DeliveryScheduleModel[] newArray(int size) {
            return new DeliveryScheduleModel[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getRef_id() {
        return ref_id;
    }

    public void setRef_id(String ref_id) {
        this.ref_id = ref_id;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getBooking_no() {
        return booking_no;
    }

    public void setBooking_no(String booking_no) {
        this.booking_no = booking_no;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(ref_id);
        parcel.writeString(dateTime);
        parcel.writeString(booking_no);
        parcel.writeString(distance);
        parcel.writeParcelable(customer, i);
        parcel.writeStringList(latlng);
        parcel.writeString(address);
        parcel.writeString(duration);
    }
}
