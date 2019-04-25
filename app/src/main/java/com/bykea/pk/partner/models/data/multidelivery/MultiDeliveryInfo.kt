package com.bykea.pk.partner.models.data.multidelivery

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Trip information for cash on delivery data model class
 */
class MultiDeliveryInfo {
    @SerializedName("is_return_run")
    var isReturnRun: Boolean = false

    @SerializedName("is_cash_on_delivery")
    var isCashOnDelivery: Boolean = false

    @SerializedName("amount")
    var amount: Int = 0

    @SerializedName("parcel_value")
    var parcelValue: Int = 0

    @SerializedName("receiver_phone")
    var receiverPhone: String? = null

    @SerializedName("receiver_name")
    var receiverName: String? = null
}