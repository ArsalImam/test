package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

public class DriverStatsData {

    private String rating;

    @SerializedName("date_range")
    private String date;

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

}