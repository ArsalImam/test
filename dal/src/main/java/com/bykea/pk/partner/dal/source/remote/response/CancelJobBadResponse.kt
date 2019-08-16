package com.bykea.pk.partner.dal.source.remote.response

data class CancelJobBadResponse(val data: ConcludeJobBadResponseData) : BaseResponse()

data class ConcludeJobBadResponseData(
        val trip_id: String
)