package com.bykea.pk.partner.models.data

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Call Driver Acknowledge Data Class
 */
class CallDriverAcknowledgeData(
        @SerializedName("batch_id")
        var batchID: String? = StringUtils.EMPTY,
        @SerializedName("driver_id")
        var driverID: String? = StringUtils.EMPTY,
        @SerializedName("trip_id")
        var tripID: String? = StringUtils.EMPTY,
        @SerializedName("is_available")
        var isAvailable: Boolean
) : Parcelable {

    /**
     * Constructor.
     *
     * Read the fields from the parcel.
     */
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt() == 1
    )

    override fun describeContents() = 0


    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(batchID)
        writeString(driverID)
        writeString(tripID)
        writeInt(if (isAvailable) 1 else 0)
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
    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<CallDriverAcknowledgeData> = object :
                Parcelable.Creator<CallDriverAcknowledgeData> {
            override fun createFromParcel(source: Parcel): CallDriverAcknowledgeData =
                    CallDriverAcknowledgeData(source)
            override fun newArray(size: Int): Array<CallDriverAcknowledgeData?> =
                    arrayOfNulls(size)
        }
    }
}