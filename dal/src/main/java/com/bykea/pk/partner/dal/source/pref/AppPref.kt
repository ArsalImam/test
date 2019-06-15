package com.bykea.pk.partner.dal.source.pref

import android.content.SharedPreferences
import com.bykea.pk.partner.dal.util.LATITUDE
import com.bykea.pk.partner.dal.util.LONGITUDE

object AppPref {

    fun getLat(pref: SharedPreferences): Double {
        return pref.getString(LATITUDE, "0.0").toDouble()
    }

    fun getLng(pref: SharedPreferences): Double {
        return pref.getString(LONGITUDE, "0.0").toDouble()
    }
}