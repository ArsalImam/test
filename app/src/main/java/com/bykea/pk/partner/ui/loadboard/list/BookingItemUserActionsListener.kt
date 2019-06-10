package com.bykea.pk.partner.ui.loadboard.list

import com.bykea.pk.partner.dal.Booking

/**
 * Listener used with data binding to process user actions.
 */
interface BookingItemUserActionsListener {

    fun onBookingClicked(booking: Booking)
}
