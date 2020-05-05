package com.bykea.pk.partner.dal.source.remote.request.nodataentry

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


/**
 * Created by Sibtain Raza on 5/4/2020.
 */
@Parcelize
class DeliveryDetailInfo : Parcelable {
    //SEND FROM CLIENT
    var cod_value: String? = null
    var order_no: String? = null
    var parcel_value: String? = null
    var voice_note: String? = null

    //RECEIVE FROM SERVER
    var alphabetical_order: String? = null
    var batch_id: String? = null
    var sequence_number: String? = null
    var trip_id: String? = null
    var trip_no: String? = null
}