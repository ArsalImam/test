package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

abstract class BaseResponse {
    var message: String = ""
    var code: Int = 0
    var subcode: Int = 0
    @SerializedName("subcode")
    private var subCode: Int = 0

    fun isSuccess(): Boolean = (code in 200..299)
}