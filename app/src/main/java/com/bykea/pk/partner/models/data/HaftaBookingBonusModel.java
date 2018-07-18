package com.bykea.pk.partner.models.data;

public class HaftaBookingBonusModel {
    
    String bonus;
    String booking;

    public HaftaBookingBonusModel(String bonus, String booking) {
        this.bonus = bonus;
        this.booking = booking;
    }

    public String getBonus() {
        return bonus;
    }

    public void setBonus(String bonus) {
        this.bonus = bonus;
    }

    public String getBooking() {
        return booking;
    }

    public void setBooking(String booking) {
        this.booking = booking;
    }
}
