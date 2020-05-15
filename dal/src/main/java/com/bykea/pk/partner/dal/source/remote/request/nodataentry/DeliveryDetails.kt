package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Created by Sibtain Raza on 4/13/2020.
 */

@Parcelize
open class DeliveryDetails : Parcelable {
    var meta: MetaData? = null
    var pickup: DeliveryDetailsLocationInfoData? = null
    var dropoff: DeliveryDetailsLocationInfoData? = null
    var details: DeliveryDetailInfo? = null
}