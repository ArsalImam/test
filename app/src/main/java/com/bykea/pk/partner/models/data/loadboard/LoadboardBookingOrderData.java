package com.bykea.pk.partner.models.data.loadboard;

import com.google.gson.annotations.SerializedName;

/**
 * data model class for loadboard ordered items of selected booking
 */
public class LoadboardBookingOrderData {

    @SerializedName("name")
    private String name;
    @SerializedName("qty")
    private int qty;
    @SerializedName("price")
    private int price;
    @SerializedName("item_id")
    private int itemId;

    public String getName() {
        return name;
    }

    public int getQty() {
        return qty;
    }

    public int getPrice() {
        return price;
    }

    public int getItemId() {
        return itemId;
    }
}
