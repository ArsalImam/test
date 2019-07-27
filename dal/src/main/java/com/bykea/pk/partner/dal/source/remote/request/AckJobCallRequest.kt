package com.bykea.pk.partner.dal.source.remote.request

open class AckJobCallRequest (
    val _id: String,
    val token_id: String,
    val lat: Double,
    val lng: Double
)