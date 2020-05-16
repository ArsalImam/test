package com.bykea.pk.partner.utils


/**
 * Created by Sibtain Raza on 4/12/2020.
 */
interface GenericListener {
    /**
     * Tapped When To Add Or Edit Delivery Details
     */
    fun addOrEditDeliveryDetails() {}

    /**
     * Navigate to Place Search Activity
     */
    fun navigateToPlaceSearch() {}

    /**
     * On user plays audio
     *
     * @param url URL of the audio file to be played
     */
    fun onPlayAudio(url: String?) {}

    /**
     * On user taps back button
     *
     */
    fun onBackClicked() {}

    /**
     * On user taps audio stops button
     *
     */
    fun onStopAudio() {}

    /**
     * When user tap to add delivery details
     */
    fun addDeliveryDetails() {}

    /**
     * When user tap on passenger top up wallet
     */
    fun topUpPassengerWallet() {}
}