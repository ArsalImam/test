package com.bykea.pk.partner.dal.source.remote.response

data class StartJobResponse(val data: StartJobResponseData) : BaseResponse()

data class StartJobResponseData(var trip_id: String, var batch_id: String)