package com.bykea.pk.partner.models.request

/**
 * Request bean for Load board Booking cancel request
 *
 * @property _id Driver Id
 * @property cancel_reason Cancellation reason
 * @property cancelled_at Time of cancellation
 * @property lat Latitude of driver at the time of cancellation
 * @property lng Longitude of driver at the time of cancellation
 * @property token_id Access token of driver
 * @property trip_id Id of trip
 *
 *
 * @author Yousuf Sohail
 */
data class LoadBoardBookingCancelRequest(
        val _id: String,
        val cancel_reason: String,
        val cancelled_at: String,
        val lat: String,
        val lng: String,
        val token_id: String,
        val trip_id: String
)