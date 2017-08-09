package com.bykea.pk.partner.models.data;

public class Settings
{
    private String cancel_time;
    private String hospital;
    private String fire_brigade;
    private String police;
    private String ambulance;
    private String videos;
    private String heatmap_refresh_timer;

    public String getCancel_time ()
    {
        return cancel_time;
    }

    public String getHospital() {
        return hospital;
    }

    public String getFire_brigade() {
        return fire_brigade;
    }

    public String getPolice() {
        return police;
    }

    public String getAmbulance() {
        return ambulance;
    }

    public String getVideos() {
        return videos;
    }

    public String getHeatmap_refresh_timer() {
        return heatmap_refresh_timer;
    }

}