package com.bykea.pk.partner.models.data;

public class MultiDeliveryPickup {

    String mArea;
    String feederName;
    String streetAddress;

    public MultiDeliveryPickup(String mArea, String feederName, String streetAddress) {
        this.mArea = mArea;
        this.feederName = feederName;
        this.streetAddress = streetAddress;
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
}
