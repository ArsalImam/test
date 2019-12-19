package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.BookingDetail

/**
 * response model for booking detail response
 * @author ArsalImam
 */
data class BookingDetailResponse (val data: BookingDetail) : BaseResponse() {}