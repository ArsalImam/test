package com.bykea.pk.partner.dal.source.remote.request

data class TemperatureSubmitRequest(
        val _id: String,
        val token_id: String,
        val temperature: Float
)