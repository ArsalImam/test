package com.bykea.pk.partner.models.response

import android.os.Parcel
import android.os.Parcelable
import com.bykea.pk.partner.models.data.MultiDeliveryCompleteRideData
import com.bykea.pk.partner.models.data.MultiDeliveryInvoiceData
import com.bykea.pk.partner.models.data.MultiDeliveryRideCompleteTripInfo
import com.google.gson.annotations.SerializedName

/**
 * Multi Delivery Complete Ride Response
 */
data class MultiDeliveryCompleteRideResponse(
        var data: MultiDeliveryCompleteRideData
) : CommonResponse()