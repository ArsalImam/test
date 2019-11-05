package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.Job

data class GetJobRequestListResponse(val data: List<Job>) : BaseResponse()