package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetailAddEditRequest
import com.bykea.pk.partner.dal.source.remote.request.nodataentry.DeliveryDetails


/**
 * Created by Sibtain Raza on 5/4/2020.
 */
class DeliveryDetailList {
    var page: Int? = null
    var limit = 10
    var total: Int? = null
    var batch_id: String? = null
    var bookings: ArrayList<DeliveryDetails>? = ArrayList()
}