package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailAddEditRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails
import com.bykea.pk.partner.dal.util.DIGIT_ZERO
import com.google.gson.annotations.SerializedName


/**
 * Created by Sibtain Raza on 5/4/2020.
 */
class DeliveryDetailList {
    var page: Int? = null
    var limit = 10
    var total: Int? = null
    var batch_id: String? = null
    var bookings: ArrayList<DeliveryDetails>? = ArrayList()
    @SerializedName("wc")
    var passWallet: Int = DIGIT_ZERO
    @SerializedName("est")
    var kraiKiKamai: Int = DIGIT_ZERO
    @SerializedName("amount")
    var codAmount: String? = null
    @SerializedName("payable")
    var cashKiWasooli: Int = DIGIT_ZERO
}