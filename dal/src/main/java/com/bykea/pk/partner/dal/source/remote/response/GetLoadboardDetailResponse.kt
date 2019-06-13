package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.Booking

data class GetLoadboardDetailResponse(val code: Int, val message: String?, val data: Booking) {
    fun isSuccess(): Boolean = (code in 200..299)
}