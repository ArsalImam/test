package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class SignUpUserData implements Parcelable {
        private ArrayList<Images> images;
        private String email;
        private String ref_number;


    protected SignUpUserData(Parcel in) {
        images = in.createTypedArrayList(Images.CREATOR);
        email = in.readString();
        ref_number = in.readString();
    }

    public static final Creator<SignUpUserData> CREATOR = new Creator<SignUpUserData>() {
        @Override
        public SignUpUserData createFromParcel(Parcel in) {
            return new SignUpUserData(in);
        }

        @Override
        public SignUpUserData[] newArray(int size) {
            return new SignUpUserData[size];
        }
    };

    public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getRef_number() {
            return ref_number;
        }

        public void setRef_number(String ref_number) {
            this.ref_number = ref_number;
        }


        public ArrayList<Images> getImages() {
            return images;
        }

        public void setImages(ArrayList<Images> images) {
            this.images = images;
        }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeTypedList(images);
        parcel.writeString(email);
        parcel.writeString(ref_number);
    }
}
