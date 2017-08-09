package com.bykea.pk.partner.models.data;

public class FeedbackData {
    private boolean is_wallet;
    private boolean is_promo;

    public boolean is_promo() {
        return is_promo;
    }

    public void setIs_promo(boolean is_promo) {
        this.is_promo = is_promo;
    }

    public boolean is_wallet() {
        return is_wallet;
    }

    public void setIs_wallet(boolean is_wallet) {
        this.is_wallet = is_wallet;
    }
}