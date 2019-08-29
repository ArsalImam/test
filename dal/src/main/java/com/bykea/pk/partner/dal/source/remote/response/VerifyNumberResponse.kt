package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

class VerifyNumberResponse : BaseResponse() {

    var link: String? = null
    @SerializedName("support")
    var supportNumber: String? = null
}
