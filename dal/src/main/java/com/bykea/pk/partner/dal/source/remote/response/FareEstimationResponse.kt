package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

/***
 * Fare Estimate API response.
 */
class FareEstimationResponse : BaseResponse() {

    //#region Getter Setter
    @SerializedName("mstr")
    var estimatedFare: String? = null

    @SerializedName("calldata")
    var callData: CallData? = null
    //endregion
}


