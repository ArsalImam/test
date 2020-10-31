package com.bykea.pk.partner.dal.source.remote.request

data class PickJobRequest(
        val lat: Double,
        val lng: Double,
        val dispatch: Boolean
)