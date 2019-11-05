package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod
import com.google.gson.annotations.SerializedName

data class GetWithdrawalPaymentMethods(
        @SerializedName("data") val data: List<WithdrawPaymentMethod>?
) : BaseResponse() {
}