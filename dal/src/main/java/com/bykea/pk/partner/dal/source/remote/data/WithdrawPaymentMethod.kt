package com.bykea.pk.partner.dal.source.remote.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class WithdrawPaymentMethod(
        @SerializedName("code") @Expose var code: Int?,
        @SerializedName("name") @Expose var name: String?,
        @SerializedName("fees") @Expose var fees: Double?,
        @SerializedName("description") @Expose var description: String?,
        @SerializedName("comments") @Expose var comments: String?
) {

    var isSelected: Boolean = false
}