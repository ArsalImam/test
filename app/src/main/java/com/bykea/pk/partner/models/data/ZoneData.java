package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class ZoneData implements Parcelable{

    private String _id;

    @SerializedName("zone")
    private String englishName;

    @SerializedName("urdu_text")
    private String urduName;

    private ArrayList<ZoneAreaData> areas;

    protected ZoneData(Parcel in) {
        _id = in.readString();
        englishName = in.readString();
        urduName = in.readString();
    }

    public static final Creator<ZoneData> CREATOR = new Creator<ZoneData>() {
        @Override
        public ZoneData createFromParcel(Parcel in) {
            return new ZoneData(in);
        }

        @Override
        public ZoneData[] newArray(int size) {
            return new ZoneData[size];
        }
    };

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getUrduName() {
        return urduName;
    }

    public void setUrduName(String urduName) {
        this.urduName = urduName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public ArrayList<ZoneAreaData> getAreas() {
        return areas;
    }

    public void setAreas(ArrayList<ZoneAreaData> areas) {
        this.areas = areas;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(englishName);
        parcel.writeString(urduName);
    }
}
