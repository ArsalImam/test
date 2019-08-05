package com.bykea.pk.partner.dal.source.remote.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class WithdrawPaymentMethod {
    @SerializedName("code")
    @Expose
    var code: Int? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("fees")
    @Expose
    var fees: Double? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
    @SerializedName("comments")
    @Expose
    var comments: String? = null

    var isSelected: Boolean = false

    init {
        isSelected = false
    }
}