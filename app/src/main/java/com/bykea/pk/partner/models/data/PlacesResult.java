package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;


public class PlacesResult implements Parcelable {
    public double latitude;
    public double longitude;
    public String name;
    public String address;
    public boolean isSaved;
    public String placeId;

    /**
     * A constructor that initializes the PlacesResult object
     **/
    public PlacesResult(String name, String address, double latitude, double longitude){
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
    }

    /**
     * Retrieving PlacesResult data from Parcel object
     * This constructor is invoked by the method createFromParcel(Parcel source) of
     * the object CREATOR
     **/
    private PlacesResult(Parcel in){
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.name = in.readString();
        this.address = in.readString();
        isSaved = in.readByte() != 0;
        placeId = in.readString();
    }

    public static final Creator<PlacesResult> CREATOR = new Creator<PlacesResult>() {

        @Override
        public PlacesResult createFromParcel(Parcel source) {
            return new PlacesResult(source);
        }

        @Override
        public PlacesResult[] newArray(int size) {
            return new PlacesResult[size];
        }
    };


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(address);
        dest.writeByte((byte) (isSaved ? 1 : 0));
        dest.writeString(placeId);
    }
}
