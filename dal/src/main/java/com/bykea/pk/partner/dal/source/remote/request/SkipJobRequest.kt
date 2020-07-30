package com.bykea.pk.partner.dal.source.remote.request

/**
 * Request bean for Load board Booking cancel request
 *
 * @property _id Driver Id
 * @property token_id Access token of driver
 * @property booking_id Booking Id of Trip
 *
 * @author Sibtain Raza
 */
data class SkipJobRequest(
        val _id: String,
        val token_id: String,
        val booking_id: Long? = null
)