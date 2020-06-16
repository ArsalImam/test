package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sibtain Raza on 5/4/2020.
 */

class MetaData() : Parcelable {
    var service_code: Int? = null
    var failed_booking_id: String? = null

    constructor(parcel: Parcel) : this() {
        service_code = parcel.readValue(Int::class.java.classLoader) as? Int
        failed_booking_id = parcel.readValue(String::class.java.classLoader) as? String
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(service_code)
        parcel.writeValue(failed_booking_id)
    }

    override fun describeContents(): Int {
        return DIGIT_ZERO
    }

    companion object CREATOR : Parcelable.Creator<MetaData> {
        override fun createFromParcel(parcel: Parcel): MetaData {
            return MetaData(parcel)
        }

        override fun newArray(size: Int): Array<MetaData?> {
            return arrayOfNulls(size)
        }
    }
}