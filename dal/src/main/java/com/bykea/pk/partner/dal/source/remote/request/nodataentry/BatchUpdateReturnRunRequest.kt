package com.bykea.pk.partner.dal.source.remote.request.nodataentry


/**
 * Created by Sibtain Raza on 5/18/2020.
 */
data class BatchUpdateReturnRunRequest(var return_run: Boolean? = null, var trip_id: String? = null, var delivery_status: Boolean = false, var delivery_message: String? = null)