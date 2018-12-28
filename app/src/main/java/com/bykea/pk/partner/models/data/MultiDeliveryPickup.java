package com.bykea.pk.partner.models.data;

public class MultiDeliveryPickup {

    String mArea;
    String feederName;
    String streetAddress;
    String contactNumber;

    public MultiDeliveryPickup(String mArea, String feederName, String streetAddress) {
        this.mArea = mArea;
        this.feederName = feederName;
        this.streetAddress = streetAddress;
    }

    public MultiDeliveryPickup(String mArea, String feederName, String streetAddress,
                               String contactNumber) {
        this.mArea = mArea;
        this.feederName = feederName;
        this.streetAddress = streetAddress;
        this.contactNumber = contactNumber;
    }

    public String getArea() {
        return mArea;
    }

    public void setArea(String mArea) {
        this.mArea = mArea;
    }

    public String getFeederName() {
        return feederName;
    }

    public void setFeederName(String feederName) {
        this.feederName = feederName;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }
}
