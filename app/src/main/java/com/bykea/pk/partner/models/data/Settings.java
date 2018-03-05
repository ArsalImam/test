package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import org.apache.commons.lang3.StringUtils;

public class Settings {
    @SerializedName("cancel_time_driver")
    private String cancel_time;
    private String videos;
    private String heatmap_refresh_timer;
    private String arrived_min_dist;

    @SerializedName("driver_app_version")
    private String app_version;

    @SerializedName("trip_support_link_driver")
    private String trip_support_link;

    @SerializedName("trip_support_max_days_driver")
    private String trip_support_max_days;
    private String demand;
    private String notice;
    private String top_up_limit;
    private String amount_limit;
    @SerializedName("terms_driver")
    private String terms;
    private String cih_range;


    public String getCancel_time() {
        return cancel_time;
    }

    public String getVideos() {
        return videos;
    }

    public String getHeatmap_refresh_timer() {
        return heatmap_refresh_timer;
    }

    public String getTrip_support_link() {
        return StringUtils.isNotBlank(trip_support_link) ? trip_support_link : "http://www.bykea.com/issues/partner.php?";
    }

    public int getTrip_support_max_days() {
        return StringUtils.isNotBlank(trip_support_max_days) ? Integer.parseInt(trip_support_max_days) : 10;
    }

    public String getDemand() {
        return StringUtils.isNotBlank(demand) ? demand : "http://www.bykea.com";

    }

    public String getNotice() {
        return StringUtils.isNotBlank(notice) ? notice : "http://www.bykea.com";

    }

    public int getTop_up_limit() {
        return StringUtils.isNotBlank(top_up_limit) ? Integer.parseInt(top_up_limit) : 500;
    }

    public int getAmount_limit() {
        return StringUtils.isNotBlank(amount_limit) ? Integer.parseInt(amount_limit) : 35000;
    }

    public String getTerms() {
        return StringUtils.isNotBlank(terms) ? terms : "https://www.bykea.com/terms.html";
    }

    public String getApp_version() {
        return app_version;
    }

    public String getCih_range() {
        return cih_range;
    }

    public void setCih_range(String cih_range) {
        this.cih_range = cih_range;
    }

    public int getArrived_min_dist() {
        return Integer.parseInt(arrived_min_dist);
    }

}