package com.bykea.pk.partner.ui.loadboard.detail


/**
 * Listener used with data binding to process user actions.
 */
interface BookingDetailUserActionsListener {
    fun onPlayAudio(url: String)
    fun onNavigateToMap(lat: Double, lng: Double)
    fun onAcceptBooking()
}
