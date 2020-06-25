package com.bykea.pk.partner.dal.source.remote.response

/**
 * Model class for Fence API Response
 */
class FenceCheckResponse(var data: FenceData? = null) : BaseResponse() {
    data class FenceData(var inFence: Boolean)
}
