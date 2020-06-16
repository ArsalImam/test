package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.util.DIGIT_ZERO

open class BaseResponseError {
    var subcode: Int? = null
    var partner_limit: Int? = null
    var remaining_limit: Int? = null
}