package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.Booking

data class GetLoadboardListingResponse(val data: List<Booking>) : BaseResponse() {
    fun isSuccess(): Boolean = (code == 200)
}