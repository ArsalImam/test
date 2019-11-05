package com.bykea.pk.partner.ui.activities

interface BookingCallListener {
    /**
     * Tapped When User Call From Phone
     */
    fun onCallOnPhone() {}

    /**
     * Tapped When User Call From Whatsapp
     */
    fun onCallOnWhatsapp() {}
}