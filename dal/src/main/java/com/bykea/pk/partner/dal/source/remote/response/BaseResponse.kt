package com.bykea.pk.partner.dal.source.remote.response

abstract class BaseResponse {
    var success: Boolean = false
    var message: String? = null
    var code: Int = 0
    var subcode: Int = 0
    var uuid: String? = null
}