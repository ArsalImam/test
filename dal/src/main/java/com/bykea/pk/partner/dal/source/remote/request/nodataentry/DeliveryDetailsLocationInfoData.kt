package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING
import kotlinx.android.parcel.Parcelize

@Parcelize
open class DeliveryDetailsLocationInfoData(
        var lng: String = EMPTY_STRING,
        var lat: String = EMPTY_STRING,
        var address: String? = EMPTY_STRING) : Parcelable