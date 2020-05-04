package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import com.bykea.pk.partner.dal.util.EMPTY_STRING
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils

/**
 * Created by Sibtain Raza on 4/13/2020.
 */

@Parcelize
data class DeliveryDetails(
        var _id: String = EMPTY_STRING,
        var user_type: String = EMPTY_STRING,
        var token_id: String = EMPTY_STRING,

        var mobile_number: String? = null,
        var consignee_name: String? = null,
        var complete_address: String? = null,
        var gps_address: String? = null,
        var parcel_value: String? = null,
        var order_number: String? = null,
        var cod_amount: String? = null,

        var deliverySequence: String = StringUtils.EMPTY,
        var dropZoneNameUr: String = StringUtils.EMPTY,
        var bookingNo: String = StringUtils.EMPTY,

        var trip: DeliveryDetailsTrip = DeliveryDetailsTrip(),

        var pickup_info: DeliveryDetailsLocationInfoData = DeliveryDetailsLocationInfoData(),
        var dropoff_info: DeliveryDetailsLocationInfoData? = null
) : Parcelable