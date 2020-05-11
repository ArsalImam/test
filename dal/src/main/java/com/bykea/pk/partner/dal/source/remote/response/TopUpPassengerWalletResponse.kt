package com.bykea.pk.partner.dal.source.remote.response


class TopUpPassengerWalletResponse : BaseResponse() {
    var data: TopUpData? = null

    class TopUpData {
        var amount: Int = 0
    }
}
