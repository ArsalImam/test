package com.bykea.pk.partner.models.response

/**
 * Response bean for Google Geo Code Api
 */
data class GeoCodeApiResponse(
        val results: List<Result>,
        val status: String
)

data class Result(
        val formatted_address: String,
        val geometry: Geometry
)

data class Geometry(
        val location: Location
)

data class Location(
        val lat: Double,
        val lng: Double
)