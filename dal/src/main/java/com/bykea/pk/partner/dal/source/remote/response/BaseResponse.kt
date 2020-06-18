package com.bykea.pk.partner.dal.source.remote.response

open class BaseResponse {
    var message: String = ""
    var code: Int = 0
    var subcode: Int = 0
    var pagination: Pagination? = null
    var error: Any? = null

    fun isSuccess(): Boolean = (code in 200..299)
}