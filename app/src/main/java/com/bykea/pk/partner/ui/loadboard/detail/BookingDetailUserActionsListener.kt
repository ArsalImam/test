package com.bykea.pk.partner.ui.loadboard.detail


/**
 * Listener used with data binding to process user actions.
 */
interface BookingDetailUserActionsListener {

    /**
     * On user plays audio
     *
     * @param url URL of the audio file to be played
     */
    fun onPlayAudio(url: String?)

    /**
     * On navigation to Google map for route
     *
     * @param lat Latitude
     * @param lng Longitude
     */
    fun onNavigateToMap(pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double)

    /**
     * On user attempt to accept booking
     *
     */
    fun onAcceptBooking()
    fun onBackClicked()
}
