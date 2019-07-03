package com.bykea.pk.partner.dal.source.pref

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.util.EMPTY_STRING

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

    fun getIsCash(pref: SharedPreferences): Boolean {
        return pref.getBoolean(CASH, false)
    }

    private const val LATITUDE = "latitude"
    private const val LONGITUDE = "longitude"
    private const val DRIVER_ID = "DRIVER_ID"
    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val CASH = "CASH"
}
