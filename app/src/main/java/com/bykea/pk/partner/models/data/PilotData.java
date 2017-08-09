package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;


public class PilotData {

    @SerializedName("_id")
    private String id;
    @SerializedName("token_id")
    private String accessToken;
    @SerializedName("full_name")
    private String fullName;
    private String email;
    @SerializedName("img_id")
    private String pilotImage;
    @SerializedName("phone")
    private String phoneNo;
    private String pincode;
    private String vendor_id;
    private boolean is_vendor;
    private City city;
    private String address;
    @SerializedName("i_agree")
    private boolean termsAndConditions;
    @SerializedName("plate_no")
    private String plateNo;
    @SerializedName("driver_license_number")
    private String licenseNo;
    @SerializedName("dirver_license_image_id")
    private String licenseImage;
    @SerializedName("license_expire")
    private String licenseExpiry;
    @SerializedName("vehicle_type")
    private String vehicleType;
    private String cnic;
    private String picture;
    @SerializedName("is_available")
    private boolean available;
    @SerializedName("status")
    private boolean verified;

    private String rating;
    private String service_type;

    private String lat;
    private String lng;

    private String verified_trips;
    private String acceptance_rate;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getPilotImage() {
        return pilotImage;
    }

    public void setPilotImage(String pilotImage) {
        this.pilotImage = pilotImage;
    }

    public String getLicenseImage() {
        return licenseImage;
    }

    public void setLicenseImage(String licenseImage) {
        this.licenseImage = licenseImage;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getFullName() {
        return StringUtils.isNotBlank(fullName) ? StringUtils.capitalize(fullName) : StringUtils.EMPTY;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isTermsAndConditions() {
        return termsAndConditions;
    }

    public void setTermsAndConditions(boolean termsAndConditions) {
        this.termsAndConditions = termsAndConditions;
    }

    public String getPlateNo() {
        return plateNo;
    }

    public void setPlateNo(String plateNo) {
        this.plateNo = plateNo;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(String licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getCnic() {
        return cnic;
    }

    public void setCnic(String cnic) {
        this.cnic = cnic;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAcceptance_rate() {
        return acceptance_rate;
    }

    public void setAcceptance_rate(String acceptance_rate) {
        this.acceptance_rate = acceptance_rate;
    }

    public String getVerified_trips() {
        return verified_trips;
    }

    public void setVerified_trips(String verified_trips) {
        this.verified_trips = verified_trips;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public boolean is_vendor() {
        return StringUtils.isNotBlank(getVendor_id());
    }

    public void setIs_vendor(boolean is_vendor) {
        this.is_vendor = is_vendor;
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public void setVendor_id(String vendor_id) {
        this.vendor_id = vendor_id;
    }

    public class City {
        private String _id;
        private String name;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
