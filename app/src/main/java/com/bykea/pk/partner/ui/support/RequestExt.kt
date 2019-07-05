package com.bykea.pk.partner.ui.support

import com.bykea.pk.partner.utils.Constants
import zendesk.support.CustomField

fun getCancellationReason(customerFields: List<CustomField>) =
        " " + customerFields.find { it.id == Constants.ZendeskCustomFields.Cancellation_Reason }?.value.toString()