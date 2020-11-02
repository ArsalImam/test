package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

data class BookingUpdated(
        val trip_id: String,
        @SerializedName("is_paid") val isPaid: Boolean
) : BaseResponse()