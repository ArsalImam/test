package com.bykea.pk.partner.ui.loadboard.list

import com.bykea.pk.partner.dal.Job

/**
 * Listener used with data binding to process user actions.
 *
 * @author
 */
interface JobListItemActionsListener {

    /**
     * On user click the item of Booking listing to navigate to [Job] detail screen
     *
     * @param job [Job] object
     */
    fun onBookingClicked(job: Job)
}
