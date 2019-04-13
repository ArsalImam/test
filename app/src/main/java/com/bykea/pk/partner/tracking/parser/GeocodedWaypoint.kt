package com.bykea.pk.partner.tracking.parser

data class GeocodedWaypoint(
        val geocoder_status: String,
        val place_id: String,
        val types: List<String>
)