package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

/***
 * Fare Estimate API response.
 */
class FareEstimationResponse : BaseResponse() {
    @SerializedName("data")
    var fareEstimateData: FareEstimateData? = null
}


