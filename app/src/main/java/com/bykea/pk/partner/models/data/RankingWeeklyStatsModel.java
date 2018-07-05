package com.bykea.pk.partner.models.data;

public class RankingWeeklyStatsModel {

    String bonus;
    String bookingCount;

    public RankingWeeklyStatsModel(String bonus, String bookingCount) {
        this.bonus = bonus;
        this.bookingCount = bookingCount;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getBookingCount() {
        return bookingCount;
    }

    public void setBookingCount(String bookingCount) {
        this.bookingCount = bookingCount;
    }
}
