package com.bykea.pk.partner.models.data;

public class ShahkarModel {

    private int number;
    private String name;
    private int booking;
    private float score;
    private int kamai;

    public ShahkarModel(int number, String name, int booking, float score, int kamai) {
        this.number = number;
        this.name = name;
        this.booking = booking;
        this.score = score;
        this.kamai = kamai;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBooking() {
        return booking;
    }

    public void setBooking(int booking) {
        this.booking = booking;
    }

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public int getKamai() {
        return kamai;
    }

    public void setKamai(int kamai) {
        this.kamai = kamai;
    }
}
