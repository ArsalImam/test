package com.bykea.pk.partner.ui.loadboard.list

import com.bykea.pk.partner.dal.Booking

/**
 * Listener used with data binding to process user actions.
 *
 * @author
 */
interface JobRequestListItemActionsListener {

    /**
     * On user click the item of Booking listing to navigate to [Booking] detail screen
     *
     * @param booking [Booking] object
     */
    fun onBookingClicked(booking: Booking)
}
