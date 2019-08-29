package com.bykea.pk.partner.dal.source.remote.response

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName

/**
 * Call Data response from Socket event.
 */
class CallData() : Parcelable {

    //region Getter Setter
    @SerializedName("dist")
    var distance: Double = 0.toDouble()

    @SerializedName("max_limit")
    var maxLimitPrice: Double = 0.toDouble()

    @SerializedName("time")
    var estimateTime: Int = 0

    constructor(parcel: Parcel) : this() {
        distance = parcel.readDouble()
        maxLimitPrice = parcel.readDouble()
        estimateTime = parcel.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(distance)
        parcel.writeDouble(maxLimitPrice)
        parcel.writeInt(estimateTime)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallData> {
        override fun createFromParcel(parcel: Parcel): CallData {
            return CallData(parcel)
        }

        override fun newArray(size: Int): Array<CallData?> {
            return arrayOfNulls(size)
        }
    }
}
