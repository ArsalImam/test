package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.Booking

data class GetLoadboardDetailResponse(val status: Int, val msg: String?, val data: Booking) {
    fun isSuccess(): Boolean = (status in 200..299)
}