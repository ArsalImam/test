package com.bykea.pk.partner.models.data

import com.google.gson.annotations.SerializedName

/**
 * Multiple Delivery Remaining ETA Data Class
 *
 * @param tripID The trip id of the batch.
 * @param remainingTime The remaining time
 * @param remainingDistance The remaining distance.
 */
data class MultipleDeliveryRemainingETA (
        @SerializedName("trip_id")
        var tripID: String? = null,
        @SerializedName("est_remaining_time")
        var remainingTime: Int = 0,
        @SerializedName("est_remaining_distance")
        var remainingDistance: Double = 0.toDouble()
)