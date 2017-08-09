package com.bykea.pk.partner.models.data;

public class DriverStatsData {
    private String acceptance_rate;
    private String rating;
    private String verified_trips;

    public String getAcceptance_rate() {
        return acceptance_rate;
    }

    public void setAcceptance_rate(String acceptance_rate) {
        this.acceptance_rate = acceptance_rate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getVerified_trips() {
        return verified_trips;
    }

    public void setVerified_trips(String verified_trips) {
        this.verified_trips = verified_trips;
    }
}