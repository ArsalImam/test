package com.bykea.pk.partner.ui.loadboard.list

import com.bykea.pk.partner.dal.JobRequest

/**
 * Listener used with data binding to process user actions.
 *
 * @author
 */
interface JobRequestListItemActionsListener {

    /**
     * On user click the item of Booking listing to navigate to [JobRequest] detail screen
     *
     * @param jobRequest [JobRequest] object
     */
    fun onBookingClicked(jobRequest: JobRequest)
}
