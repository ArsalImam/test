package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;


public class PlacesResult implements Parcelable {
    public double latitude;
    public double longitude;
    public String name;
    public String address;

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    /**
     * Storing the PlacesResult data to Parcel object
     **/
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(name);
        dest.writeString(address);
    }

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
}
