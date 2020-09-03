package com.bykea.pk.partner.dal.source.remote.response

import com.google.gson.annotations.SerializedName

data class DriverSettingsResponse(
        var data: DriverSettings? = null
) : BaseResponse()

data class DriverSettings(
        var access_key: String? = null,
        var secret_access_key: String? = null,
        var identity_pool_uid: String? = null,
        var bucket_name: String? = null,
        @SerializedName("kronos_get_bookings_for_driver")
        var bookingLisitingForDriverUrl: String? = null,
        @SerializedName("kronos_get_bookings_by_id")
        var bookingDetailByIdUrl: String? = null,
        @SerializedName("kronos_partner_summary")
        var kronosPartnerSummary: String? = null,
        @SerializedName("kronos_partner_invoice")
        var feedbackInvoiceListingUrl: String? = null,
        @SerializedName("kronos_partner_batch_invoice")
        var batchBookingInvoiceUrl: String? = null,
        @SerializedName("s3_pool_id")
        var s3PoolId: String? = null,
        @SerializedName("s3_bucket_pod")
        var s3BucketPod: String? = null,
        @SerializedName("s3_bucket_voice_notes")
        var s3BucketVoiceNotes: String? = null,
        @SerializedName("s3_bucket_region")
        var s3BucketRegion: String? = null) {
    var demand: String? = null
        get() {
            return if (demand.isNullOrEmpty())
                return "http://www.bykea.com"
            else
                field
        }
}