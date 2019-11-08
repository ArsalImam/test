package com.bykea.pk.partner.dal.source.remote.request.ride

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING

data class RideCreateRequestObject(
        var trip: RideCreateTripData = RideCreateTripData(),
        var pickup_info: RideCreateLocationInfoData = RideCreateLocationInfoData(),
        var dropoff_info: RideCreateLocationInfoData? = null,
        var _id: String = EMPTY_STRING,
        var user_type: String = EMPTY_STRING,
        var token_id: String = EMPTY_STRING) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readParcelable(RideCreateTripData::class.java.classLoader),
            parcel.readParcelable(RideCreateLocationInfoData::class.java.classLoader),
            parcel.readParcelable(RideCreateLocationInfoData::class.java.classLoader),
            parcel.readString(),
            parcel.readString(),
            parcel.readString()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(trip, flags)
        parcel.writeParcelable(pickup_info, flags)
        parcel.writeParcelable(dropoff_info, flags)
        parcel.writeString(_id)
        parcel.writeString(user_type)
        parcel.writeString(token_id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RideCreateRequestObject> {
        override fun createFromParcel(parcel: Parcel): RideCreateRequestObject {
            return RideCreateRequestObject(parcel)
        }

        override fun newArray(size: Int): Array<RideCreateRequestObject?> {
            return arrayOfNulls(size)
        }
    }
}