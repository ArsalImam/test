package com.bykea.pk.partner.dal.source.remote.response

data class ArriveAtJobResponse(val data: ArriveAtJobResponseData) : BaseResponse()

data class ArriveAtJobResponseData(val trip_id: String)