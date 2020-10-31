package com.bykea.pk.partner.dal.source.remote.request.ride

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING

open class RideCreateLocationInfoData(
        var lat: String? = EMPTY_STRING,
        var lng: String? = EMPTY_STRING,
        var address: String? = EMPTY_STRING) : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(lat)
        parcel.writeString(lng)
        parcel.writeString(address)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RideCreateLocationInfoData> {
        override fun createFromParcel(parcel: Parcel): RideCreateLocationInfoData {
            return RideCreateLocationInfoData(parcel)
        }

        override fun newArray(size: Int): Array<RideCreateLocationInfoData?> {
            return arrayOfNulls(size)
        }
    }
}