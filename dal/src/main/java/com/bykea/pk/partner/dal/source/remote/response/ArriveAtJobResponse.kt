package com.bykea.pk.partner.dal.source.remote.response

data class ArriveAtJobResponse(val data: ArriveAtJobResponseData) : BaseResponse()

data class ArriveAtJobResponseData(var trip_id: String?, var batch_id: String?)