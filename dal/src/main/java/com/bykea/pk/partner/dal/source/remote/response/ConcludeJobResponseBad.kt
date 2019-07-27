package com.bykea.pk.partner.dal.source.remote.response

data class ConcludeJobResponseBad(val data: Data) : BaseResponse()

data class Data(
        val trip_id: String
)