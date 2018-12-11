package com.bykea.pk.partner.models.data;

import android.graphics.Bitmap;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;

public class MultiDeliveryDropOff {
    String mArea;
    String streetAddress;
    String dropOffNumberText;

    public MultiDeliveryDropOff(String mArea, String streetAddress, String dropOffNumberText) {
        this.mArea = mArea;
        this.streetAddress = streetAddress;
        this.dropOffNumberText = dropOffNumberText;
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

    public String getDropOffNumberText() {
        return dropOffNumberText;
    }

    public void setDropOffNumberText(String dropOffNumberText) {
        this.dropOffNumberText = dropOffNumberText;
    }
}
