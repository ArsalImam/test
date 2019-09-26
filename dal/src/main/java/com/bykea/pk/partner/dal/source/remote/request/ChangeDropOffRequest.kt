package com.bykea.pk.partner.dal.source.remote.request

data class ChangeDropOffRequest(
        val _id: String,
        val token_id: String,
        val dropoff_info: Stop
) {
    data class Stop(
            val lat: Double,
            val lng: Double,
            val address: String)
}