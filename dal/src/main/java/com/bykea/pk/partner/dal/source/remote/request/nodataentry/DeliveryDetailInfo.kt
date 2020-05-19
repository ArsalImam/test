package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.bykea.pk.partner.dal.util.PARTNER_ANDROID
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sibtain Raza on 5/4/2020.
 */

class DeliveryDetailInfo() : Parcelable {
    //SEND FROM CLIENT
    var cod_value: String? = null
    var order_no: String? = null
    var parcel_value: String? = null
    var voice_note: String? = null

    //RECEIVE FROM SERVER
    var batch_id: String? = null
    var display_tag: String? = null
    var trip_id: String? = null
    var trip_no: String? = null
    var status: String? = null
    var delivery_status: Boolean? = null
    var creator: String? = PARTNER_ANDROID

    constructor(parcel: Parcel) : this() {
        cod_value = parcel.readString()
        order_no = parcel.readString()
        parcel_value = parcel.readString()
        voice_note = parcel.readString()
        batch_id = parcel.readString()
        display_tag = parcel.readString()
        trip_id = parcel.readString()
        trip_no = parcel.readString()
        status = parcel.readString()
        creator = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(cod_value)
        parcel.writeString(order_no)
        parcel.writeString(parcel_value)
        parcel.writeString(voice_note)
        parcel.writeString(batch_id)
        parcel.writeString(display_tag)
        parcel.writeString(trip_id)
        parcel.writeString(trip_no)
        parcel.writeString(status)
        parcel.writeString(creator)
    }

    override fun describeContents(): Int {
        return DIGIT_ZERO
    }

    companion object CREATOR : Parcelable.Creator<DeliveryDetailInfo> {
        override fun createFromParcel(parcel: Parcel): DeliveryDetailInfo {
            return DeliveryDetailInfo(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryDetailInfo?> {
            return arrayOfNulls(size)
        }
    }
}