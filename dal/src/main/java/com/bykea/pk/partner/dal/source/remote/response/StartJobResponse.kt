package com.bykea.pk.partner.dal.source.remote.response

data class StartJobResponse(val data: StartJobResponseData) : BaseResponse()

data class StartJobResponseData(val trip_id: String)