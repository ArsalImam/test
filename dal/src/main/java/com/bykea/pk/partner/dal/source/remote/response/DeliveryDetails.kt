package com.bykea.pk.partner.dal.source.remote.response

import org.apache.commons.lang3.StringUtils


/**
 * Created by Sibtain Raza on 4/13/2020.
 * smsibtainrn@gmail.com
 */
data class DeliveryDetails(
        var deliverySequence: String = StringUtils.EMPTY,
        var dropZoneNameUr: String = StringUtils.EMPTY,
        var bookingNo: String = StringUtils.EMPTY
)