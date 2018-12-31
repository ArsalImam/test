package com.bykea.pk.partner.models.response

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.models.data.CallDriverAcknowledgeData

/**
 * Call Driver Acknowledge Response
 */
open class MultiDeliveryCallDriverAcknowledgeResponse(
    var data: CallDriverAcknowledgeData? = null
) : CommonResponse(), Parcelable {


    /**
     * Constructor.
     *
     * Read the fields from the parcel.
     */
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
    companion object CREATOR : Parcelable.Creator<MultiDeliveryCallDriverAcknowledgeResponse> {
        override fun createFromParcel(parcel: Parcel): MultiDeliveryCallDriverAcknowledgeResponse {
            return MultiDeliveryCallDriverAcknowledgeResponse(parcel)
        }

        override fun newArray(size: Int): Array<MultiDeliveryCallDriverAcknowledgeResponse?> {
            return arrayOfNulls(size)
        }
    }
}
