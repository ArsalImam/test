package com.bykea.pk.partner.dal.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class CityBannerResponse(
        @SerializedName("data")
        var cityBanners: ArrayList<CityBanner>? = null,
        var settingsVersion: String? = null,
        var lat: Double? = null,
        var lng: Double? = null
) : BaseResponse(), Parcelable