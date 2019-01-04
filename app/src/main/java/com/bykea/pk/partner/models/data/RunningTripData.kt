package com.bykea.pk.partner.models.data

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Running Trip Data
 */
class RunningTripData<T> {
    @SerializedName("type")
    var type: String? = StringUtils.EMPTY

    @SerializedName("trip")
    var trip: T? = null
}