package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

class CancelJobResponse : BaseResponse() {

    @SerializedName("is_available")
    private val available: String? = null

    val isAvailable: Boolean
        get() = available!!.isEmpty() || java.lang.Boolean.parseBoolean(available)
}