package com.bykea.pk.partner.dal.source.pref

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.util.*

object AppPref {

    fun getLat(pref: SharedPreferences): Double {
        return pref.getString(LATITUDE, "0.0").toDouble()
    }

    fun getLng(pref: SharedPreferences): Double {
        return pref.getString(LONGITUDE, "0.0").toDouble()
    }

    fun getDriverId(pref: SharedPreferences): String {
        return pref.getString(DRIVER_ID, EMPTY_STRING)
    }

    fun getAccessToken(pref: SharedPreferences): String {
        return pref.getString(ACCESS_TOKEN, EMPTY_STRING)
    }

}