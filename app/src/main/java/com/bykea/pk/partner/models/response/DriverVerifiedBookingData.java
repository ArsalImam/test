package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class DriverVerifiedBookingData {

    @SerializedName("count")
    private int count = 1;

    @SerializedName("date")
    private String date = "";

    public int getBookingsCount() {
        return count;
    }

    public void setBookingsCount(int bookingsCount) {
        this.count = bookingsCount;
    }

    public String getBookingsTime() {
        return date;
    }

    public void setBookingsTime(String bookingsTime) {
        this.date = bookingsTime;
    }
}
