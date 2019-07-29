package com.bykea.pk.partner.dal.source.remote.response

data class AckJobCallResponse(val data: AckJobCallResponseData) : BaseResponse()

data class AckJobCallResponseData(val trip_id: String)