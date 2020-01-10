package com.bykea.pk.partner.dal.source.remote.request

import com.bykea.pk.partner.dal.LocCoordinatesInTrip

data class FinishJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val address: String?,
        val route: ArrayList<LocCoordinatesInTrip>
)