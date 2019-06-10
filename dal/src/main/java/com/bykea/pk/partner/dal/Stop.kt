package com.bykea.pk.partner.dal

import androidx.room.Embedded

data class Stop(
        val lat: Double,
        val lng: Double,
        val duration_est: Int,
        val distance_est: Int,
        val zone_name_en: String?,
        val zone_name_ur: String?,
        @Embedded val contact: Contact
)