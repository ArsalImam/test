package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

/**
 * Data model class for loadboard jobs - zone data
 */
public class LoadBoardListingZoneData {

    @SerializedName("zone")
    private String englishName;
    @SerializedName("urdu_text")
    private String urduName;

    private int code;

    public String getEnglishName() {
        return englishName;
    }

    public String getUrduName() {
        return urduName;
    }

    public int getCode() {
        return code;
    }
}
