package com.bykea.pk.partner.location

import com.bykea.pk.partner.dal.source.pref.AppPref

object Utils {

    fun canSendLocation(): Boolean {
        return AppPref.isLoggedIn() && (AppPref.getAvailableStatus() ||
                AppPref.isOutOfFence() || AppPref.isOnTrip())
    }
}