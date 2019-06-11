package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Model class to represent pick, drop or any other stop of driver
 */
public class Stop {

    @SerializedName("duration_est")
    private Integer duration;

    @SerializedName("distance_est")
    private Integer distance;

    @SerializedName("zone_name_en")
    private String zoneNameEn;

    @SerializedName("zone_name_ur")
    private String zoneNameUr;

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getDistance() {
        return distance;
    }

    public void setDistance(Integer distance) {
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
}
