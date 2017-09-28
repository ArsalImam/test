package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class DriverStatsData {
    @SerializedName("accRate")
    private String acceptanceRate;
    @SerializedName("dRating")
    private String dailyRating;
    private String rating;
    @SerializedName("rides")
    private String trips;
    private String date;
    private String time;

    public String getAcceptanceRate() {
        return acceptanceRate;
    }

    public void setAcceptanceRate(String acceptanceRate) {
        this.acceptanceRate = acceptanceRate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getTrips() {
        return trips;
    }

    public void setTrips(String trips) {
        this.trips = trips;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDailyRating() {
        return dailyRating;
    }
}