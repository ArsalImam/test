package com.bykea.pk.partner.models.data;

public class DileveryScheduleModel {
    private String address;
    private String duration;
    private String distance;

    public DileveryScheduleModel(String address, String duration, String distance) {
        this.address = address;
        this.duration = duration;
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }
}
