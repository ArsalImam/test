package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.DIGIT_ZERO

/**
 * Created by Sibtain Raza on 4/13/2020.
 */

open class DeliveryDetails() : Parcelable {
    var meta: MetaData? = null
    var pickup: DeliveryDetailsLocationInfoData? = null
    var dropoff: DeliveryDetailsLocationInfoData? = null
    var details: DeliveryDetailInfo? = null

    constructor(parcel: Parcel) : this() {
        meta = parcel.readParcelable(MetaData::class.java.classLoader)
        pickup = parcel.readParcelable(DeliveryDetailsLocationInfoData::class.java.classLoader)
        dropoff = parcel.readParcelable(DeliveryDetailsLocationInfoData::class.java.classLoader)
        details = parcel.readParcelable(DeliveryDetailInfo::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(meta, flags)
        parcel.writeParcelable(pickup, flags)
        parcel.writeParcelable(dropoff, flags)
        parcel.writeParcelable(details, flags)
    }

    override fun describeContents(): Int {
        return DIGIT_ZERO
    }

    companion object CREATOR : Parcelable.Creator<DeliveryDetails> {
        override fun createFromParcel(parcel: Parcel): DeliveryDetails {
            return DeliveryDetails(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryDetails?> {
            return arrayOfNulls(size)
        }
    }
}