package com.bykea.pk.partner.models.response

/**
 * Booking accept Api response call bean class
 *
 * @author Yousuf Sohail
 */
data class BookingAcceptedResponse(
        val driver_id: String,
        val passenger_id: String,
        val status: String,
        val trip_id: String
) : CommonResponse()