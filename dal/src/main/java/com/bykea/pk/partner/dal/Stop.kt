package com.bykea.pk.partner.dal

import java.io.Serializable

data class Stop(
        val address: String?,
        val zone_en: String?,
        val zone_ur: String?,
        val lat: Double,
        val lng: Double,
        val distance: Int,
        val duration: Int
) : Serializable