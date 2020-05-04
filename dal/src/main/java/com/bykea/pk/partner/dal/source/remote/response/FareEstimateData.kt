package com.bykea.pk.partner.dal.source.remote.response

import android.os.Parcel
import android.os.Parcelable

import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

/**
 * Call Data response from Socket event.
 */
@Parcelize
class FareEstimateData : Parcelable {
    @SerializedName("fare")
    var maxLimitPrice: Double = 0.toDouble()
}
