package com.bykea.pk.partner.dal.source.remote.response

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils

/**
 * City wise banner model
 */
@Parcelize
data class CityBanner(
        @SerializedName("img_url")
        var imgURL: String? = null,
        @SerializedName("service_code")
        var serviceCode: String? = null,
        @SerializedName("tag")
        var departmentTag: String? = null
) : Parcelable