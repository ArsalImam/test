package com.bykea.pk.partner.models.data;

import android.graphics.Bitmap;

public class MultiDeliveryDropOff {
    String mArea;
    String streetAddress;
    Bitmap dropOffImage;

    public MultiDeliveryDropOff(String mArea, String streetAddress) {
        this.mArea = mArea;
        this.streetAddress = streetAddress;
    }

    public String getmArea() {
        return mArea;
    }

    public void setmArea(String mArea) {
        this.mArea = mArea;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public Bitmap getDropOffImage() {
        return dropOffImage;
    }

    public void setDropOffImage(Bitmap dropOffImage) {
        this.dropOffImage = dropOffImage;
    }
}
