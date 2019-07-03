package com.bykea.pk.partner.ui.support

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
}