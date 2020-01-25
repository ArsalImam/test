package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

class SkipJobResponse : BaseResponse() {

    @SerializedName("is_available")
    private val available: String? = null
}