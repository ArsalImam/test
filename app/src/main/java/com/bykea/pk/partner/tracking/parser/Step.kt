package com.bykea.pk.partner.tracking.parser

data class Step(
        val distance: Distance,
        val duration: Duration,
        val end_location: Location,
        val html_instructions: String,
        val maneuver: String,
        val polyline: Polyline,
        val start_location: Location,
        val travel_mode: String
)