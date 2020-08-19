package com.bykea.pk.partner.models.data

/**
 * osm geocode response model
 */
data class OSMGeoCode(
        var address: Address?,
        var boundingbox: List<String>?,
        var display_name: String?,
        var lat: String?,
        var licence: String?,
        var lon: String?,
        var osm_id: Long?,
        var osm_type: String?,
        var place_id: Int?
)

/**
 * address model require from osm response model
 */
data class Address(
        var city: String?,
        var country: String?,
        var country_code: String?,
        var county: String?,
        var postcode: String?,
        var state: String?,
        var town: String?
)