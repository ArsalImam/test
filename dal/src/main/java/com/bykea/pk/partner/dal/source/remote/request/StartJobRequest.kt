package com.bykea.pk.partner.dal.source.remote.request

data class StartJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val address: String
)