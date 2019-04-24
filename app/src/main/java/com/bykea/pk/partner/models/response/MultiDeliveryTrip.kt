package com.bykea.pk.partner.models.response

import com.bykea.pk.partner.models.data.multidelivery.MultiDeliveryInfo
import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multiple Delivery Trip Class
 */
class MultiDeliveryTrip {
    var id: String? = StringUtils.EMPTY

    @SerializedName("trip_no")
    var tripNo: String? = StringUtils.EMPTY

    var type: String? = StringUtils.EMPTY

    @SerializedName("trip_status_code")
    var tripStatusCode: Int = 0

    var status: String? = StringUtils.EMPTY

    @SerializedName("start_address")
    var startAddress: String? = StringUtils.EMPTY

    @SerializedName("end_address")
    var endAddress: String? = StringUtils.EMPTY

    @SerializedName("trip_duration")
    var tripDuration: String? = StringUtils.EMPTY

    @SerializedName("trip_distance")
    var tripDistance: String? = StringUtils.EMPTY
    
    @SerializedName("zone_dropoff_name")
    var zoneDropOffName: String = StringUtils.EMPTY

    @SerializedName("zone_dropoff_name_urdu")
    var zoneDropOffNameUrdu: String = StringUtils.EMPTY

    //current trip info object
    @SerializedName("delivery_info")
    var deliveryInfo: MultiDeliveryInfo? = null
}