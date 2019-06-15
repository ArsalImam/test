package com.bykea.pk.partner.models.request

data class LoadBoardRideCancelRequest(
        val _id: String,
        val cancel_reason: String,
        val cancelled_at: String,
        val lat: String,
        val lng: String,
        val token_id: String,
        val trip_id: String
)