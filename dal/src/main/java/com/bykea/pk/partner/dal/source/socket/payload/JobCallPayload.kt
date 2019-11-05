package com.bykea.pk.partner.dal.source.socket.payload

import com.bykea.pk.partner.dal.Stop
import java.io.Serializable

data class JobCallPayload(
        val trip: JobCall,
        val type: String
)

data class JobCall(
        val trip_id: String,
        val booking_no: String,
        val service_code: Int,
        val fare_est: Int?,
        val customer_id: String,
        val pickup: Stop,
        val dropoff: Stop?,
        val timer: Int,
        val dt: String //ISO 8601
) : Serializable