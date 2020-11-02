package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

public class Images implements Parcelable {
        private String type;
        private String link;

        protected Images(Parcel in) {
            type = in.readString();
            link = in.readString();
        }

        public static final Creator<Images> CREATOR = new Creator<Images>() {
            @Override
            public Images createFromParcel(Parcel in) {
                return new Images(in);
            }

            @Override
            public Images[] newArray(int size) {
                return new Images[size];
            }
        };

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getLink() {
            return link;
        }

        public void setLink(String link) {
            this.link = link;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(type);
            parcel.writeString(link);
        }
    }