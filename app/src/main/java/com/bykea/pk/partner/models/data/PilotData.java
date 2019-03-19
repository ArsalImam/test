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
    private String vendor_id;
    private City city;

    @SerializedName("license_expire")
    private String licenseExpiry;
    @SerializedName("is_available")
    private boolean available;

    private String rating;
    private String service_type;

    private String lat;
    private String lng;
    private String reg_id;

    @SerializedName("cih")
    private String cashInHand;

    /**
     * Added cash field for loadboard working - non-cash drivers will not be able to see/call loadboard jobs
     */
    private boolean cash;

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

    public String getPhonePlusSign() {
        return "+" + phoneNo;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }



    public String getLicenseExpiry() {
        return licenseExpiry;
    }

    public void setLicenseExpiry(String licenseExpiry) {
        this.licenseExpiry = licenseExpiry;
    }

    public boolean is_vendor() {
        return StringUtils.isNotBlank(getVendor_id());
    }

    public String getVendor_id() {
        return vendor_id;
    }

    public int getCashInHand() {
        return StringUtils.isNoneBlank(cashInHand) ? Integer.parseInt(cashInHand) : 0;
    }

    public String getReg_id() {
        return reg_id;
    }

    public void setReg_id(String reg_id) {
        this.reg_id = reg_id;
    }

    public String getService_type() {
        return service_type;
    }

    public void setService_type(String service_type) {
        this.service_type = service_type;
    }

    public boolean isCash() {
        return cash;
    }

    public void setCash(boolean cash) {
        this.cash = cash;
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
            return StringUtils.isNotBlank(name) ? name : StringUtils.EMPTY;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
