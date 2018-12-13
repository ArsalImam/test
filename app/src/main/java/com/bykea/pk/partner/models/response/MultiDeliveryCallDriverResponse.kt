package com.bykea.pk.partner.models.response

import com.google.android.gms.common.util.CollectionUtils.mutableListOf
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multi Delivery Call Driver Response Class
 */
data class MultiDeliveryCallDriverResponse(
        var pickup: MultipleDeliveryPickupResponse? = null,
        var bookings: List<MultipleDeliveryBookingResponse>? = mutableListOf(),
        @SerializedName("status")
        var batchStatus: String? = StringUtils.EMPTY,
        @SerializedName("batch_id")
        var batchID: String? = StringUtils.EMPTY,
        @SerializedName("batch_number")
        var batchNumber: Int? = 0,
        @SerializedName("icon")
        var imageURL: String? = StringUtils.EMPTY,
        @SerializedName("created_at")
        var createdAt: String? = StringUtils.EMPTY,
        @SerializedName("driver_id")
        var driverID: String? = StringUtils.EMPTY,
        //timer in seconds
        var timer: Int? = 0,
        //distance in meter
        @SerializedName("est_total_distance")
        var estTotalDistance: Float? = 0f,
        //duration in seconds
        @SerializedName("est_total_duration")
        var estTotalDuration: Int? = 0,
        @SerializedName("est_fare")
        var estFare: Int? = 0,
        @SerializedName("est_cash_collection")
        var estCashCollection: Int? = 0
)