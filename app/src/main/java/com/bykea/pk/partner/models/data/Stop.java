package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import static com.bykea.pk.partner.utils.Constants.NEGATIVE_DIGIT_ONE;

/**
 * Model class to represent pick, drop or any other stop of driver
 */
public class Stop {

    @SerializedName("duration_est")
    private Integer duration;

    @SerializedName("distance_est")
    private float distance = NEGATIVE_DIGIT_ONE;

    @SerializedName(value = "zone_name_en", alternate = {"zone_en"})
    private String zoneNameEn;

    @SerializedName(value = "zone_name_ur", alternate = {"zone_ur"})
    private String zoneNameUr;

    private String address;
    private float lat;
    private float lng;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public int getDistance() {
        return (int) distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getZoneNameEn() {
        return zoneNameEn;
    }

    public void setZoneNameEn(String zoneNameEn) {
        this.zoneNameEn = zoneNameEn;
    }

    public String getZoneNameUr() {
        return zoneNameUr;
    }

    public void setZoneNameUr(String zoneNameUr) {
        this.zoneNameUr = zoneNameUr;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(float lng) {
        this.lng = lng;
    }
}
