package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName
import com.instabug.library.util.StringUtility
import org.apache.commons.lang3.StringUtils

/**
 * Multiple Delivery Pickup Response
 */
open class MultipleDeliveryPickupResponse {
    var lat: Float? = 24.7797059f
    var lng: Float? = 67.0527337f
    @SerializedName("formatted")
    var pickupAddress: String? = StringUtils.EMPTY
    var distance: Int? = 0 //distance in meter
    var duration: Int? = 0 //duration in seconds
    @SerializedName("name")
    var feederName: String? = StringUtils.EMPTY
    @SerializedName("contact_number")
    var contactNumer: String? = StringUtils.EMPTY
}
