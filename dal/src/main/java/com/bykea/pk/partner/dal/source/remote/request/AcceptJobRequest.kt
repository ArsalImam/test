package com.bykea.pk.partner.dal.source.remote.request

data class AcceptJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val accept_timer_seconds: Int
)