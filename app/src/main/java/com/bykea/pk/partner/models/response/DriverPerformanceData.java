package com.bykea.pk.partner.models.response;

import com.google.gson.annotations.SerializedName;

public class DriverPerformanceData {

    @SerializedName("booking")
    private int driverBooking;

    private int completedBooking;
    private String weeklyBalance;

    private String totalBalance;

    @SerializedName("time")
    private String driverOnTime;

    private float weeklyRating;
    private float totalRating;
    private int acceptancePercentage;
    private int completedPercentage;
    private int cancelPercentage;

    public int getDriverBooking() {
        return driverBooking;
    }

    public void setDriverBooking(int driverBooking) {
        this.driverBooking = driverBooking;
    }

    public int getCompletedBooking() {
        return completedBooking;
    }

    public void setCompletedBooking(int completedBooking) {
        this.completedBooking = completedBooking;
    }

    public String getWeeklyBalance() {
        return weeklyBalance;
    }

    public void setWeeklyBalance(String weeklyBalance) {
        this.weeklyBalance = weeklyBalance;
    }

    public String getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(String totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getDriverOnTime() {
        return driverOnTime;
    }

    public void setDriverOnTime(String driverOnTime) {
        this.driverOnTime = driverOnTime;
    }

    public float getWeeklyRating() {
        return weeklyRating;
    }

    public void setWeeklyRating(float weeklyRating) {
        this.weeklyRating = weeklyRating;
    }

    public float getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(float totalRating) {
        this.totalRating = totalRating;
    }

    public int getAcceptancePercentage() {
        return acceptancePercentage;
    }

    public void setAcceptancePercentage(int acceptancePercentage) {
        this.acceptancePercentage = acceptancePercentage;
    }

    public int getCompletedPercentage() {
        return completedPercentage;
    }

    public void setCompletedPercentage(int completedPercentage) {
        this.completedPercentage = completedPercentage;
    }

    public int getCancelPercentage() {
        return cancelPercentage;
    }

    public void setCancelPercentage(int cancelPercentage) {
        this.cancelPercentage = cancelPercentage;
    }
}
