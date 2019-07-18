package com.bykea.pk.partner.dal.source.remote.request

data class ConcludeJobRequest(
        val _id: String,
        val token_id: String,
        val lat: Double,
        val lng: Double,
        val rate: Int,
        val received_amount: Int,
        val delivery_message: String?,
        val delivery_status: Boolean?,
        val purchase_amount: Int?,
        val received_by_name: String?,
        val received_by_phone: String?
)
