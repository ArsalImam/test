package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SignUpCity implements Parcelable {
    private String _id;
    private String video;

    @SerializedName("name_ur")
    private String urduName;

    private String name;

    private ArrayList<Double> gps;

    protected SignUpCity(Parcel in) {
        _id = in.readString();
        urduName = in.readString();
        video = in.readString();
        name = in.readString();
    }

    public static final Creator<SignUpCity> CREATOR = new Creator<SignUpCity>() {
        @Override
        public SignUpCity createFromParcel(Parcel in) {
            return new SignUpCity(in);
        }

        @Override
        public SignUpCity[] newArray(int size) {
            return new SignUpCity[size];
        }
    };

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrduName() {
        return urduName;
    }

    public void setUrduName(String urduName) {
        this.urduName = urduName;
    }

    public ArrayList<Double> getGps() {
        return gps;
    }

    public void setGps(ArrayList<Double> gps) {
        this.gps = gps;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(urduName);
        parcel.writeString(video);
        parcel.writeString(name);
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }
}
