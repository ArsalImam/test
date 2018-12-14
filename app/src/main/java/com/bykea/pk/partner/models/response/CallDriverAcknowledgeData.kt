package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Call Driver Acknowledge Data Class
 */
class CallDriverAcknowledgeData(
        @SerializedName("batch_id")
        var batchID: String? = StringUtils.EMPTY,
        @SerializedName("driver_id")
        var driverID: String? = StringUtils.EMPTY
)
