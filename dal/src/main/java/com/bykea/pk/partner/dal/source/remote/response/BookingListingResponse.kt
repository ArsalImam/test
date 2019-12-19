package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.source.remote.data.BookingList

data class BookingListingResponse(var data: BookingListData? = BookingListData()) : BaseResponse()

data class BookingListData(var result: ArrayList<BookingList>? = ArrayList(), var pagination: Pagination? = Pagination(0))