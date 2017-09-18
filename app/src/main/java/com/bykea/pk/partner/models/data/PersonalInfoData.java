package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PersonalInfoData implements Serializable {
    private String phone;
    private String mobile_2;
    private String mobile_1;
    private String brand;
    private String horse_power;
    private String model_number;
    private String chassis_number;
    private String engine_number;
    private String account_number;
    private String account_title;
    private String finance;

    private String plate_no;


    private String driver_license_number;

    private String city;
    private String registration_date;

    private String excise_verified;

    private String license_expire;
    private String license_city;


    private String img_id;

    private String email;

    private String address;

    private String full_name;

    private String cnic;

    //TODO Update Home Lat/Lng keys when available in API
    @SerializedName("current_lat")
    private String homeLat;
    @SerializedName("current_lng")
    private String homeLng;

    public String getPhone() {
        return phone;
    }


    public String getPlate_no() {
        return plate_no;
    }


    public String getDriver_license_number() {
        return driver_license_number;
    }


    public String getCity() {
        return city;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public String getExcise_verified() {
        return excise_verified;
    }

    public String getLicense_expire() {
        return license_expire;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFull_name() {
        return full_name;
    }

    public String getCnic() {
        return cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImg_id() {
        return img_id;
    }

    public String getMobile_2() {
        return mobile_2;
    }

    public String getMobile_1() {
        return mobile_1;
    }

    public String getBrand() {
        return brand;
    }

    public String getHorse_power() {
        return horse_power;
    }

    public String getModel_number() {
        return model_number;
    }

    public String getChassis_number() {
        return chassis_number;
    }

    public String getEngine_number() {
        return engine_number;
    }

    public String getAccount_number() {
        return account_number;
    }

    public String getAccount_title() {
        return account_title;
    }

    public String getFinance() {
        return finance;
    }

    public String getLicense_city() {
        return license_city;
    }

    public String getHomeLat() {
        return homeLat;
    }

    public String getHomeLng() {
        return homeLng;
    }
}

		