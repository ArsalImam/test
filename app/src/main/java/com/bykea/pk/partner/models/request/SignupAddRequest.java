package com.bykea.pk.partner.models.request;

import java.util.ArrayList;

public class SignupAddRequest {

    private String phone;
    private String imei;
    private String mobile_brand;
    private String mobile_model;
    private ArrayList<Double> geoloc;
    private String city;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMobile_brand() {
        return mobile_brand;
    }

    public void setMobile_brand(String mobile_brand) {
        this.mobile_brand = mobile_brand;
    }

    public String getMobile_model() {
        return mobile_model;
    }

    public void setMobile_model(String mobile_model) {
        this.mobile_model = mobile_model;
    }

    public ArrayList<Double> getGeoloc() {
        return geoloc;
    }

    public void setGeoloc(ArrayList<Double> geoloc) {
        this.geoloc = geoloc;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
