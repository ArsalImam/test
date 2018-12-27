package com.bykea.pk.partner.models.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multi Delivery Ride Complete Trip Info
 */
class MultiDeliveryRideCompleteTripInfo() : Parcelable {

    @SerializedName("start_address")
    var startAddress: String? = StringUtils.EMPTY

    @SerializedName("end_address")
    var endAddress: String? = StringUtils.EMPTY

    @SerializedName("trip_duration")
    var tripDuration: String? = StringUtils.EMPTY

    @SerializedName("trip_distance")
    var tripDistance: String? = StringUtils.EMPTY

    @SerializedName("id")
    var tripID: String? = StringUtils.EMPTY

    constructor(parcel: Parcel) : this() {
        startAddress = parcel.readString()
        endAddress = parcel.readString()
        tripDuration = parcel.readString()
        tripDistance = parcel.readString()
        tripID = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(startAddress)
        parcel.writeString(endAddress)
        parcel.writeString(tripDuration)
        parcel.writeString(tripDistance)
        parcel.writeString(tripID)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MultiDeliveryRideCompleteTripInfo> {
        override fun createFromParcel(parcel: Parcel): MultiDeliveryRideCompleteTripInfo {
            return MultiDeliveryRideCompleteTripInfo(parcel)
        }

        override fun newArray(size: Int): Array<MultiDeliveryRideCompleteTripInfo?> {
            return arrayOfNulls(size)
        }
    }

}