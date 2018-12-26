package com.bykea.pk.partner.models.response

import android.os.Parcel
import android.os.Parcelable

/**
 * Call Driver Acknowledge Response
 */
open class MultiDeliveryCallDriverAcknowledgeResponse(
    var data: CallDriverAcknowledgeData? = null
) : CommonResponse(), Parcelable {
    constructor(parcel: Parcel) :
            this(parcel.readParcelable<CallDriverAcknowledgeData>(
                    CallDriverAcknowledgeData::class.java.classLoader)) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(data, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MultiDeliveryCallDriverAcknowledgeResponse> {
        override fun createFromParcel(parcel: Parcel): MultiDeliveryCallDriverAcknowledgeResponse {
            return MultiDeliveryCallDriverAcknowledgeResponse(parcel)
        }

        override fun newArray(size: Int): Array<MultiDeliveryCallDriverAcknowledgeResponse?> {
            return arrayOfNulls(size)
        }
    }
}
