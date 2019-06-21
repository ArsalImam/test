package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.Booking

data class GetJobRequestListResponse(val data: List<Booking>) : BaseResponse()