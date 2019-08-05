package com.bykea.pk.partner.dal.source.remote.request

import com.bykea.pk.partner.dal.LocCoordinatesInTrip

class ArrivedAtJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val route: ArrayList<LocCoordinatesInTrip>
)