package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName

/**
 * Multiple Delivery Booking Response Class
 */
class MultipleDeliveryBookingResponse {
    var trip: MultiDeliveryTrip? = null
    @SerializedName("dropoff")
    var dropOff: MultipleDeliveryDropOff? = null
    var passenger: MultiDeliveryPassenger? = null
}
