package com.bykea.pk.partner.models.data

import com.bykea.pk.partner.models.response.MultipleDeliveryBookingResponse
import com.bykea.pk.partner.models.response.MultipleDeliveryPickupResponse
import com.bykea.pk.partner.models.response.NormalCallData
import com.bykea.pk.partner.utils.TripStatus
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multi Delivery Call Driver Data Class
 */
data class MultiDeliveryCallDriverData(
        var trip_id: String?,
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

        // ALTERNATE ADDED BACAUSE OF SIMPLE RIDES
        @SerializedName("est_fare", alternate = ["fare_est"])
        var estFare: Int? = 0,
        @SerializedName("est_cash_collection")
        var estCashCollection: Int? = 0,
        var acceptTime: Long,

        // PARAMETERS ADDED TO CATER, SIMPLE RIDES
        @SerializedName("code")
        var serverCode: Int?,
        var trip_type: String?,
        var service_code: Int?,
        var booking_no: String?,
        var customer_id: String?,
        var dt: String?,
        var type: String?,
        var dropoff: MultipleDeliveryPickupResponse? = null
) {
    fun getAcceptedTime(): Long = if (acceptTime > 0) acceptTime else System.currentTimeMillis()

    /**
     * Get trip by trip id
     *
     * @param The [MultipleDeliveryBookingResponse] object.
     *
     * @return The booking object according to their trip ID.
     *
     */
    fun getTripById(tripID: String): MultipleDeliveryBookingResponse? {
        bookings?.let {
            for (booking in it) {
                if (tripID.contentEquals(booking.trip?.id!!)) {
                    return booking
                }
            }
        }
        return null
    }

    /**
     * Check for unfinished trip
     *
     * @param response The [MultiDeliveryCallDriverData] object.
     *
     * @return True if the trip status is not [TripStatus.ON_COMPLETED_TRIP] &
     * not [TripStatus.ON_FEEDBACK_TRIP] other wise its mean that there
     * is not trip available return false
     */
    fun isUnfinishedTripRemaining(response: MultiDeliveryCallDriverData): Boolean {
        val bookingResponseList = response.bookings
        for (response in bookingResponseList!!) {
            if (!response.trip!!.status!!.equals(TripStatus.ON_COMPLETED_TRIP,
                            ignoreCase = true) && !response.trip!!.status!!.equals(TripStatus.ON_FEEDBACK_TRIP, ignoreCase = true)) {
                return true
            }
        }

        return false
    }
}