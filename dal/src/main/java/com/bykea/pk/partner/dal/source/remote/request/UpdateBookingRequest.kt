package com.bykea.pk.partner.dal.source.remote.request

import androidx.room.util.StringUtil
import com.bykea.pk.partner.dal.util.EMPTY_STRING

open class UpdateBookingRequest {
    var _id: String? = EMPTY_STRING
    var token_id: String? = EMPTY_STRING
    var trip: Trip? = null
    var extra_info: ExtraInfo? = null

    open class Trip {
        var amount: Int? = null
    }

    open class ExtraInfo {
        var telco_name: String? = null
        var vendor_name: String? = null
        var bill_company_name: String? = null

        var account_number: String? = null
        var cnic: String? = null
        var iban: String? = null
        var phone: String? = null
    }
}
