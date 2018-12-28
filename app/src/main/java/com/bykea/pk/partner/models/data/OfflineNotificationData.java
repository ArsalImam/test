package com.bykea.pk.partner.models.data;

import android.os.Parcel;
import android.os.Parcelable;

/***
 * Offline Notification Data model class which would be store in
 * Shared preference when notification is received from FCM
 */
public class OfflineNotificationData implements Parcelable {
    private String message;
    private String lat;
    private String lng;
    private String event;
    private String _id;


    /***
     * Constructor which is used for Parcelable object
     * @param in Parcel object reader
     */
    protected OfflineNotificationData(Parcel in) {
        message = in.readString();
        lat = in.readString();
        lng = in.readString();
        event = in.readString();
        _id = in.readString();
    }

    //region Getter Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(String event) {
        this.event = event;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    //endregion

    //region Parcelable Helper methods
    @Override
    public int describeContents() {
        return 0;
    }


    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(message);
        parcel.writeString(lat);
        parcel.writeString(lng);
        parcel.writeString(event);
        parcel.writeString(_id);
    }

    /**
     * Parcelable creator class which would be used to tell parcelable object how to create it's Parcel object
     */
    public static final Creator<OfflineNotificationData> CREATOR = new Creator<OfflineNotificationData>() {
        @Override
        public OfflineNotificationData createFromParcel(Parcel in) {
            return new OfflineNotificationData(in);
        }

        @Override
        public OfflineNotificationData[] newArray(int size) {
            return new OfflineNotificationData[size];
        }
    };

    //endregion
}