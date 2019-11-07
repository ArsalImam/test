package com.bykea.pk.partner.ui.complain

/**
 * Interface Used For Click Listener (In Different Fragments)
 */
interface GenericFragmentListener {
    /**
     * Tapped When User Generate The Ticket
     */
    fun onSubmitClicked() {}

    /**
     * Used When User Want To See The Submitted Tickets
     */
    fun onRequestSubmittedTickets() {}

    /**
     * Use To Navigate To Home Screen On Ticket Submit Screen
     */
    fun onNavigateToHomeScreen() {}

    /**
     * Use To Cancel Bykea Cash Details Dialog
     */
    fun onCancelDialog() {}

    /**
     * Use To Update Bykea Cash Details
     */
    fun onUpdateDetails() {}
}