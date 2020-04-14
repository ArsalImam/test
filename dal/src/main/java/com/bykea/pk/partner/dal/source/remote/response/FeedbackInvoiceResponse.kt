package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.Invoice

/**
 * response model for invoice detail response
 * @author ArsalImam
 */
data class FeedbackInvoiceResponse(val data: ArrayList<Invoice>?) : BaseResponse() {}