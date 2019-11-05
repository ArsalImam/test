package com.bykea.pk.partner.dal.source.remote.response

data class AcceptJobCallResponse(val data: AcceptJobCallResponseData) : BaseResponse()

data class AcceptJobCallResponseData(val trip_id: String)