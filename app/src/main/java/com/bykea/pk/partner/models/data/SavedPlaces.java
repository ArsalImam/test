package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class SavedPlaces implements Parcelable {
    @SerializedName("id")
    private String placeId;

    @SerializedName("_id")
    private String userId;
    private String token_id;

    private double lat;
    private double lng;

    @SerializedName("add")
    private String address;

    @SerializedName("eAdd")
    private String edited_address;

    @SerializedName("eName")
    private String edited_name;

    @SerializedName("ph")
    private String phone;

    private ArrayList<Double> loc;

    public SavedPlaces() {
    }

    protected SavedPlaces(Parcel in) {
        placeId = in.readString();
        userId = in.readString();
        token_id = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
        address = in.readString();
        edited_address = in.readString();
        edited_name = in.readString();
        phone = in.readString();
    }

    public static final Creator<SavedPlaces> CREATOR = new Creator<SavedPlaces>() {
        @Override
        public SavedPlaces createFromParcel(Parcel in) {
            return new SavedPlaces(in);
        }

        @Override
        public SavedPlaces[] newArray(int size) {
            return new SavedPlaces[size];
        }
    };

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken_id() {
        return token_id;
    }

    public void setToken_id(String token_id) {
        this.token_id = token_id;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEdited_address() {
        return edited_address;
    }

    public void setEdited_address(String edited_address) {
        this.edited_address = edited_address;
    }

    public String getEdited_name() {
        return edited_name;
    }

    public void setEdited_name(String edited_name) {
        this.edited_name = edited_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Double> getLoc() {
        return loc;
    }

    public void setLoc(ArrayList<Double> loc) {
        this.loc = loc;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(placeId);
        parcel.writeString(userId);
        parcel.writeString(token_id);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
        parcel.writeString(address);
        parcel.writeString(edited_address);
        parcel.writeString(edited_name);
        parcel.writeString(phone);
    }
}
