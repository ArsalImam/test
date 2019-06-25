package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.JobRequest

data class GetJobRequestListResponse(val data: List<JobRequest>) : BaseResponse()