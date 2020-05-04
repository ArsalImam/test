package com.bykea.pk.partner.dal.source.remote.request

/**
 * Request bean for Load board Booking cancel request
 *
 * @property _id Driver Id
 * @property cancel_reason Cancellation reason
 * @property lat Latitude of driver at the time of cancellation
 * @property lng Longitude of driver at the time of cancellation
 * @property token_id Access token of driver
 * @property trip_id Id of trip
 *
 *
 * @author ArsalImam
 */
data class CancelBatchJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val cancel_reason: String
)