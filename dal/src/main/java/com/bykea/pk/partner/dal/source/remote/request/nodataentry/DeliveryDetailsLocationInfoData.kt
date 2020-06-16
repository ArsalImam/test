package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.google.gson.annotations.Expose
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sibtain Raza on 5/4/2020.
 */

class DeliveryDetailsLocationInfoData() : Parcelable {
    var name: String? = null
    var phone: String? = null
    var lat: Double? = null
    var lng: Double? = null
    var address: String? = null
    var gps_address: String? = null
    var zone_dropoff_name: String? = null
    var zone_dropoff_name_urdu: String? = null

    constructor(parcel: Parcel) : this() {
        name = parcel.readString()
        phone = parcel.readString()
        lat = parcel.readValue(Double::class.java.classLoader) as? Double
        lng = parcel.readValue(Double::class.java.classLoader) as? Double
        address = parcel.readString()
        gps_address = parcel.readString()
        zone_dropoff_name = parcel.readString()
        zone_dropoff_name_urdu = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeValue(lat)
        parcel.writeValue(lng)
        parcel.writeString(address)
        parcel.writeString(gps_address)
        parcel.writeString(zone_dropoff_name)
        parcel.writeString(zone_dropoff_name_urdu)
    }

    override fun describeContents(): Int {
        return DIGIT_ZERO
    }

    companion object CREATOR : Parcelable.Creator<DeliveryDetailsLocationInfoData> {
        override fun createFromParcel(parcel: Parcel): DeliveryDetailsLocationInfoData {
            return DeliveryDetailsLocationInfoData(parcel)
        }

        override fun newArray(size: Int): Array<DeliveryDetailsLocationInfoData?> {
            return arrayOfNulls(size)
        }
    }
}