package com.bykea.pk.partner.dal.source.pref

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.bykea.pk.partner.dal.util.EMPTY_STRING

object AppPref {

    private lateinit var mPref: SharedPreferences

    fun initialize(context: Context) {
        mPref = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun getLat(): Double {
        return mPref.getString(LATITUDE, "0.0").toDouble()
    }

    fun getLng(): Double {
        return mPref.getString(LONGITUDE, "0.0").toDouble()
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

    fun isLoggedIn(): Boolean {
        return mPref.getBoolean(LOGIN_STATUS, false)
    }

    fun getAvailableStatus(): Boolean {
        return mPref.getBoolean(AVAILABLE_STATUS, false)
    }

    fun isOutOfFence(): Boolean {
        return mPref.getBoolean(IS_OUT_OF_FENCE, false)
    }

    fun isOnTrip(): Boolean {
        return mPref.getBoolean(ON_TRIP, false)
    }

    private const val LOGIN_STATUS = "LOGIN_STATUS"
    private const val AVAILABLE_STATUS = "AVAILABLE_STATUS"
    private const val IS_OUT_OF_FENCE = "IS_OUT_OF_FENCE"
    private const val ON_TRIP = "on_trip"
    private const val LATITUDE = "latitude"
    private const val LONGITUDE = "longitude"
    private const val DRIVER_ID = "DRIVER_ID"
    private const val ACCESS_TOKEN = "ACCESS_TOKEN"
    private const val CASH = "CASH"
}
