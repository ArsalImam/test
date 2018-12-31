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

    /**
     * Constructor.
     *
     * Read the fields from the parcel.
     */
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

    /**
     * Declare object inside class using companion object.
     *
     * Interface that must be implemented and provided as a public CREATOR
     * field that generates instances of your Parcelable class from a Parcel.
     *
     * Call back to be invoked when creating a new instance of the Parcelable class, instantiating it
     * from the given Parcel whose data had previously been written.
     *
     */
    companion object CREATOR : Parcelable.Creator<MultiDeliveryRideCompleteTripInfo> {
        override fun createFromParcel(parcel: Parcel): MultiDeliveryRideCompleteTripInfo {
            return MultiDeliveryRideCompleteTripInfo(parcel)
        }

        override fun newArray(size: Int): Array<MultiDeliveryRideCompleteTripInfo?> {
            return arrayOfNulls(size)
        }
    }

}