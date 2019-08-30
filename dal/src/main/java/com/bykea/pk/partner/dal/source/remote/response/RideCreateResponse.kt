package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.util.EMPTY_STRING

class RideCreateResponse : BaseResponse() {

    var data: ResponseData? = null

    inner class ResponseData {
        var trip_id = EMPTY_STRING
        var trip_no = EMPTY_STRING
        var passenger_id = EMPTY_STRING
    }
}