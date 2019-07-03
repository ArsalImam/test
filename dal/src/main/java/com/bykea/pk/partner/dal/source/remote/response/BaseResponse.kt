package com.bykea.pk.partner.dal.source.remote.response

abstract class BaseResponse {
    var message: String = ""
    var code: Int = 0

    fun isSuccess(): Boolean = (code in 200..299)
}