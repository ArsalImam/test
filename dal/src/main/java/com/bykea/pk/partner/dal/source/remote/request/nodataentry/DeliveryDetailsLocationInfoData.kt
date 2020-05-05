package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sibtain Raza on 5/4/2020.
 */
@Parcelize
class DeliveryDetailsLocationInfoData : Parcelable {
    var name: String? = null
    var phone: String? = null
    var lat: Double? = null
    var lng: Double? = null
    var address: String? = null
    var gps_address: String? = null
}