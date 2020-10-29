package com.bykea.pk.partner.models.data;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SignUpSettingsResponse {
    private int code;
    private String main_video;
    private String image_base_url;
    private long timeStamp;
    @SerializedName("cities")
    private ArrayList<SignUpCity> city;
    private SignUpSettingsRecord records;

    public ArrayList<SignUpCity> getCity() {
        return city;
    }

    public void setCity(ArrayList<SignUpCity> city) {
        this.city = city;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMain_video() {
        return main_video;
    }

    public void setMain_video(String main_video) {
        this.main_video = main_video;
    }

    public String getImage_base_url() {
        return image_base_url;
    }

    public void setImage_base_url(String image_base_url) {
        this.image_base_url = image_base_url;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public SignUpSettingsRecord getRecords() {
        return records;
    }
}
