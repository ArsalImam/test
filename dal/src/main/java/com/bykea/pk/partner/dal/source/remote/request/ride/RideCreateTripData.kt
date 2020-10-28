package com.bykea.pk.partner.dal.source.remote.request.ride

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING

open class RideCreateTripData(
        var code: String? = EMPTY_STRING,
        var service_code: Int = -1,
        var creator: String? = EMPTY_STRING,
        var lat: String? = EMPTY_STRING,
        var lng: String? = EMPTY_STRING):Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(code)
        parcel.writeInt(service_code)
        parcel.writeString(creator)
        parcel.writeString(lat)
        parcel.writeString(lng)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RideCreateTripData> {
        override fun createFromParcel(parcel: Parcel): RideCreateTripData {
            return RideCreateTripData(parcel)
        }

        override fun newArray(size: Int): Array<RideCreateTripData?> {
            return arrayOfNulls(size)
        }
    }
}