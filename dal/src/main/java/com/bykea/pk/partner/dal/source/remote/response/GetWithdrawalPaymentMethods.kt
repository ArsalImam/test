package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.WithdrawPaymentMethod

class GetWithdrawalPaymentMethods : BaseResponse() {

    var data: List<WithdrawPaymentMethod>? = null
}