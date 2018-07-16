package com.bykea.pk.partner.models.response;

import com.dft.onyxcamera.licensing.License;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LoadBoardBody {

    String _id;
    String ref_id;

    @SerializedName("dt")
    String dateTime;

    String booking_no;

    String distance;

    @SerializedName("customer")
    CustomerResponse customerResponses;

    @SerializedName("loc")
    List<String> latlng;

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

    public CustomerResponse getCustomerResponses() {
        return customerResponses;
    }

    public void setCustomerResponses(CustomerResponse customerResponses) {
        this.customerResponses = customerResponses;
    }

    public List<String> getLatlng() {
        return latlng;
    }

    public void setLatlng(List<String> latlng) {
        this.latlng = latlng;
    }
}
