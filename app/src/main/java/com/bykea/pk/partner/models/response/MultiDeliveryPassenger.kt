package com.bykea.pk.partner.models.response

import org.apache.commons.lang3.StringUtils

class MultiDeliveryPassenger {
    var name: String? = StringUtils.EMPTY
    var phone: String? = StringUtils.EMPTY
    var wallet: String? = StringUtils.EMPTY
    var rating: Int? = 0
}