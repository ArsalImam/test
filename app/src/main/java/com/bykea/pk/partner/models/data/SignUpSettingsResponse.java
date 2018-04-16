package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SignUpSettingsResponse {
    @SerializedName("status")
    private int code;
    private String main_video;
    private String image_base_url;

    private ArrayList<SignUpCity> city;

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

    public class Videocity {
        private String video;

        private String city;

        public String getVideo() {
            return video;
        }

        public void setVideo(String video) {
            this.video = video;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

    }

}
