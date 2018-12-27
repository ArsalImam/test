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
        var isAvailable: Boolean,
        @SerializedName("trip")
        var tripInfo: MultiDeliveryRideCompleteTripInfo? = null,
        var invoice: MultiDeliveryInvoiceData? = null
) : Parcelable {
    /**
     *
     */
    constructor(source: Parcel) : this(
            source.readString(),
            source.readString(),
            source.readString(),
            source.readInt() == 1,
    source.readParcelable<MultiDeliveryRideCompleteTripInfo>(
                    MultiDeliveryRideCompleteTripInfo::class.java.classLoader
            ),
            source.readParcelable<MultiDeliveryInvoiceData>(
                    MultiDeliveryInvoiceData::class.java.classLoader
            )
    )

    override fun describeContents() = 0

    /**
     *
     */
    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(batchID)
        writeString(driverID)
        writeString(tripID)
        writeInt(if (isAvailable) 1 else 0)
        writeParcelable(tripInfo, 0)
        writeParcelable(invoice, 0)
    }

    /**
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