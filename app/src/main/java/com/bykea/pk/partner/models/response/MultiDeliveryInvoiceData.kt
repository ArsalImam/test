package com.bykea.pk.partner.models.response

import com.google.gson.annotations.SerializedName
import org.apache.commons.lang3.StringUtils

/**
 * Multi Delivery Invoice Data
 */
class MultiDeliveryInvoiceData {
    @SerializedName("trip_charges")
    var tripCharges: Int? = 0

    @SerializedName("received_amount")
    var receivedAmount: Int? = 0

    @SerializedName("remaining_amount")
    var remainingAmount: Int? = 0

    @SerializedName("wallet_deduction")
    var walletDeduction: Int? = 0

    @SerializedName("promo_deduction")
    var promoDeduction: Int? = 0

    @SerializedName("driver_payout")
    var driverPayout: Int? = 0

    @SerializedName("is_deleted")
    var isDeleted: Boolean? = false

    @SerializedName("_id")
    var _id: String? = StringUtils.EMPTY

    @SerializedName("admin_fee")
    var adminFee: Int? = 0

    @SerializedName("trip_id")
    var tripID: String? = StringUtils.EMPTY

    @SerializedName("passenger_id")
    var passengerID: String? = StringUtils.EMPTY

    @SerializedName("driver_id")
    var driverID: String? = StringUtils.EMPTY

    @SerializedName("trip_no")
    var tripNo: String? = StringUtils.EMPTY

    @SerializedName("wc_value")
    var walletCreditValue: Int? = 0

    @SerializedName("wait_mins")
    var waitMins: Int? = 0

    @SerializedName("minutes")
    var minutes: Float? = 0f

    @SerializedName("total")
    var total: Int? = 0

    @SerializedName("start_balance")
    var startBalance: Int? = 0

    @SerializedName("created_at")
    var createdAt: String? = StringUtils.EMPTY

    @SerializedName("__v")
    var mongoDbVersion: Int? = 0



}