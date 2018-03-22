package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.apache.commons.lang3.StringUtils;

public class CitiesData implements Parcelable {
    private String _id;

    private String name;

    private String created_at;

    private String lng;

    private String city_code;
    private String code;

    private String lat;


    private boolean is_from;
    private boolean is_to;
    private String[] loc;

    public CitiesData() {
    }

    protected CitiesData(Parcel in) {
        _id = in.readString();
        name = in.readString();
        created_at = in.readString();
        lng = in.readString();
        city_code = in.readString();
        code = in.readString();
        lat = in.readString();
        is_from = in.readByte() != 0;
        is_to = in.readByte() != 0;
        loc = in.createStringArray();
    }

    public static final Creator<CitiesData> CREATOR = new Creator<CitiesData>() {
        @Override
        public CitiesData createFromParcel(Parcel in) {
            return new CitiesData(in);
        }

        @Override
        public CitiesData[] newArray(int size) {
            return new CitiesData[size];
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

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getCity_code() {
        return city_code;
    }

    public void setCity_code(String city_code) {
        this.city_code = city_code;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "ClassPojo [_id = " + _id + ", name = " + name + ", created_at = " + created_at + ", lng = " + lng + ", city_code = " + city_code + ", lat = " + lat + "]";
    }

    public String[] getLoc() {
        return loc;
    }

    public void setLoc(String[] loc) {
        this.loc = loc;
    }

    public boolean is_to() {
        return is_to;
    }

    public void setIs_to(boolean is_to) {
        this.is_to = is_to;
    }

    public boolean is_from() {
        return is_from;
    }

    public void setIs_from(boolean is_from) {
        this.is_from = is_from;
    }

    public String getCode() {
        return StringUtils.isNotBlank(code) ? code : "0";
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(_id);
        parcel.writeString(name);
        parcel.writeString(created_at);
        parcel.writeString(lng);
        parcel.writeString(city_code);
        parcel.writeString(code);
        parcel.writeString(lat);
        parcel.writeByte((byte) (is_from ? 1 : 0));
        parcel.writeByte((byte) (is_to ? 1 : 0));
        parcel.writeStringArray(loc);
    }
}

