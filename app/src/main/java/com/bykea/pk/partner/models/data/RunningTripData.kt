package com.bykea.pk.partner.models.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Running Trip Data
 */

class RunningTripData() : Parcelable {
    @SerializedName("type")
    var type: String? = StringUtils.EMPTY

    @SerializedName("trip")
    var trip: JsonElement? = null

    /**
     * @constructor
     *
     * @param parcel The parcel object.
     *
     */
    constructor(parcel: Parcel) : this() {
        type = parcel.readString()
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param parcel The [Parcel] The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written. May be 0 or [Parcelable][PARCELABLE_WRITE_RETURN_VALUE]
     *
     */
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(type)
    }


    /**
     * Describe the kinds of special objects contained in this
     * [Parcelable] instance's marshaled representation.
     */
    override fun describeContents(): Int {
        return 0
    }

    /**
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     */
    companion object CREATOR : Parcelable.Creator<RunningTripData> {
        override fun createFromParcel(parcel: Parcel): RunningTripData {
            return RunningTripData(parcel)
        }

        override fun newArray(size: Int): Array<RunningTripData?> {
            return arrayOfNulls(size)
        }
    }
}