package com.bykea.pk.partner.models.data

import com.google.gson.annotations.SerializedName

/**
 * Multi Delivery Complete Ride Data
 */
data class MultiDeliveryCompleteRideData (
    @SerializedName("batch_id")
    var batchID: String? = null,
    @SerializedName("trip")
    var tripInfo: MultiDeliveryRideCompleteTripInfo? = null,
    @SerializedName("invoice")
    var invoice: MultiDeliveryInvoiceData? = null
)