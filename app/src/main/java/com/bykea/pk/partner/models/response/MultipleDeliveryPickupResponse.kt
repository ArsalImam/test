package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multiple Delivery Pickup Response
 */
open class MultipleDeliveryPickupResponse {
    var lat: Float? = 0.0f
    var lng: Float? = 0.0f
    @SerializedName("formatted")
    var pickupAddress: String? = StringUtils.EMPTY
    var distance: Int? = 0 //distance in meter
    var duration: Int? = 0 //duration in seconds
    @SerializedName("name")
    var feederName: String? = StringUtils.EMPTY
    @SerializedName("contact_number")
    var contactNumer: String? = StringUtils.EMPTY
}
