package com.bykea.pk.partner.ui.loadboard.detail


/**
 * Listener used with data binding to process user actions.
 */
interface JobRequestDetailActionsListener {

    /**
     * On user plays audio
     *
     * @param url URL of the audio file to be played
     */
    fun onPlayAudio(url: String?)

    /**
     * On navigation to Google map for route
     * @param isPickUp Called For Current Location To PickUp Or PickUp to DropOff
     * @param lat Latitude
     * @param lng Longitude
     */
    fun onNavigateToMap(isPickUp: Boolean, pickLat: Double, pickLng: Double, dropLat: Double, dropLng: Double)

    /**
     * On user attempt to accept booking
     *
     */
    fun onAcceptBooking()

    /**
     * On user taps back button
     *
     */
    fun onBackClicked()

    /**
     * On user taps audio stops button
     *
     */
    fun onStopAudio()
}
