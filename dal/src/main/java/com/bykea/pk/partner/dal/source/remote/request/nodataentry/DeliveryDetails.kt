package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sibtain Raza on 4/13/2020.
 */

@Parcelize
open class DeliveryDetails : Parcelable {
    val meta: MetaData? = null
    val pickup: DeliveryDetailsLocationInfoData? = null
    val dropoff: DeliveryDetailsLocationInfoData? = null
    val details: DeliveryDetailInfo? = null
}