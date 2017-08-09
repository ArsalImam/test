package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class PersonalInfoData implements Serializable {
    private String phone;
    private String mobile_2;
    private String mobile_1;
    private String verification_date;
    private String brand;
    private String horse_power;
    private String model_number;
    private String chassis_number;
    private String engine_number;
    private String bank_name;
    private String account_number;
    private String account_title;
    private String finance;

    private String plate_no;

    private String vehicle_type;

    private String notification_status;

    private String device_type;

    private String token_id;

    private String wallet;

    private String driver_license_number;

    private String city;

    private String is_logged_in;

    private String _id;

    private String driver_status;

    private String registration_date;

    private String excise_verified;

    private String created_at;

    private String license_expire;
    private String license_city;
    private String last_login_time;

    private String status;

    private String i_agree;

    private String __v;

    private String pin_code;

    private String cnic_img_id;
    private String img_id;

    private String socket_id;

    private String is_deleted;

    private String updated_at;
    private String email;

    private String is_available;

    private String address;

    private String reg_id;

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

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlate_no() {
        return plate_no;
    }

    public void setPlate_no(String plate_no) {
        this.plate_no = plate_no;
    }

    public String getVehicle_type() {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        this.vehicle_type = vehicle_type;
    }

    public String getNotification_status() {
        return notification_status;
    }

    public void setNotification_status(String notification_status) {
        this.notification_status = notification_status;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public String getWallet() {
        return wallet;
    }

    public void setWallet(String wallet) {
        this.wallet = wallet;
    }

    public String getDriver_license_number() {
        return driver_license_number;
    }

    public void setDriver_license_number(String driver_license_number) {
        this.driver_license_number = driver_license_number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getIs_logged_in() {
        return is_logged_in;
    }

    public void setIs_logged_in(String is_logged_in) {
        this.is_logged_in = is_logged_in;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDriver_status() {
        return driver_status;
    }

    public void setDriver_status(String driver_status) {
        this.driver_status = driver_status;
    }

    public String getRegistration_date() {
        return registration_date;
    }

    public void setRegistration_date(String registration_date) {
        this.registration_date = registration_date;
    }

    public String getExcise_verified() {
        return excise_verified;
    }

    public void setExcise_verified(String excise_verified) {
        this.excise_verified = excise_verified;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLicense_expire() {
        return license_expire;
    }

    public void setLicense_expire(String license_expire) {
        this.license_expire = license_expire;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getI_agree() {
        return i_agree;
    }

    public void setI_agree(String i_agree) {
        this.i_agree = i_agree;
    }

    public String get__v() {
        return __v;
    }

    public void set__v(String __v) {
        this.__v = __v;
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }


    public void setCnic_img_id(String cnic_img_id) {
        this.cnic_img_id = cnic_img_id;
    }

    public String getSocket_id() {
        return socket_id;
    }

    public void setSocket_id(String socket_id) {
        this.socket_id = socket_id;
    }

    public String getIs_deleted() {
        return is_deleted;
    }

    public void setIs_deleted(String is_deleted) {
        this.is_deleted = is_deleted;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }


    public String getIs_available() {
        return is_available;
    }

    public void setIs_available(String is_available) {
        this.is_available = is_available;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCnic_img_id() {
        return cnic_img_id;
    }

    public String getImg_id() {
        return img_id;
    }

    public void setImg_id(String img_id) {
        this.img_id = img_id;
    }

    public String getMobile_2() {
        return mobile_2;
    }

    public void setMobile_2(String mobile_2) {
        this.mobile_2 = mobile_2;
    }

    public String getMobile_1() {
        return mobile_1;
    }

    public void setMobile_1(String mobile_1) {
        this.mobile_1 = mobile_1;
    }

    public String getVerification_date() {
        return verification_date;
    }

    public void setVerification_date(String verification_date) {
        this.verification_date = verification_date;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getHorse_power() {
        return horse_power;
    }

    public void setHorse_power(String horse_power) {
        this.horse_power = horse_power;
    }

    public String getModel_number() {
        return model_number;
    }

    public void setModel_number(String model_number) {
        this.model_number = model_number;
    }

    public String getChassis_number() {
        return chassis_number;
    }

    public void setChassis_number(String chassis_number) {
        this.chassis_number = chassis_number;
    }

    public String getEngine_number() {
        return engine_number;
    }

    public void setEngine_number(String engine_number) {
        this.engine_number = engine_number;
    }

    public String getBank_name() {
        return bank_name;
    }

    public void setBank_name(String bank_name) {
        this.bank_name = bank_name;
    }

    public String getAccount_number() {
        return account_number;
    }

    public void setAccount_number(String account_number) {
        this.account_number = account_number;
    }

    public String getAccount_title() {
        return account_title;
    }

    public void setAccount_title(String account_title) {
        this.account_title = account_title;
    }

    public String getFinance() {
        return finance;
    }

    public void setFinance(String finance) {
        this.finance = finance;
    }

    public String getLicense_city() {
        return license_city;
    }

    public void setLicense_city(String license_city) {
        this.license_city = license_city;
    }

    public String getHomeLat() {
        return homeLat;
    }

    public void setHomeLat(String homeLat) {
        this.homeLat = homeLat;
    }

    public String getHomeLng() {
        return homeLng;
    }

    public void setHomeLng(String homeLng) {
        this.homeLng = homeLng;
    }
}

		