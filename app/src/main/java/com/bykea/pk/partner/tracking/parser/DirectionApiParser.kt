package com.bykea.pk.partner.tracking.parser

data class DirectionApiParserResponse(
        val geocoded_waypoints: List<GeocodedWaypoint>,
        val routes: List<Route>,
        val status: String
)