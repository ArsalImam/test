package com.bykea.pk.partner.models.response

/**
 * Response bean for Google Geo Code Api
 */
data class GeoCodeApiResponse(
        val results: ArrayList<Result>,
        val status: String
)

data class Result(
        val formatted_address: String,
        val address_components: List<AddressComponent>,
        val geometry: Geometry
)

data class AddressComponent(
        val long_name: String,
        val short_name: String,
        val types: List<String>
)

data class Geometry(
        val location: Location
)

data class Location(
        val lat: Double,
        val lng: Double
)