package com.bykea.pk.partner.dal.source.remote.response

data class FinishJobResponse(val data: FinishJobResponseData) : BaseResponse()

data class FinishJobResponseData(
        val __v: Int,
        val _id: String,
        val admin_fee: Int,
        val base_fare: Int,
        val created_at: String,
        val driver_id: String,
        val driver_payout: Int,
        val is_deleted: Boolean,
        val km: Int,
        val minutes: Int,
        val passenger_id: String,
        val price_km: Int,
        val price_per_minute: Int,
        val promo_deduction: Int,
        val received_amount: Int,
        val remaining_amount: Int,
        val start_balance: Int,
        val total: Int,
        val trip_charges: Int,
        val trip_id: String,
        val trip_no: String,
        val wait_mins: Int,
        val wallet_deduction: Int,
        val wc_value: Int
)