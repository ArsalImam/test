package com.bykea.pk.partner.dal.source.remote.response

import com.bykea.pk.partner.dal.util.BYKEA_DOMAIN
import com.google.gson.annotations.SerializedName

data class DriverSettingsResponse(
        var data: DriverSettings? = null
) : BaseResponse()

data class DriverSettings(
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
        @SerializedName("identity_pool_uid")
        var s3PoolId: String? = null,
        @SerializedName("s3_bucket_pod")
        var s3BucketPod: String? = null,
        @SerializedName("google_places_server_api_key")
        var googlePlacesServerApiKey: String? = null,
        @SerializedName("tellotalk_access_key")
        var telloTalkAccessKey: String? = null,
        @SerializedName("tellotalk_project_token")
        var telloTalkProjectToken: String? = null,
        @SerializedName("google_maps_api_key")
        var googleMapsApiKey: String? = null,
        @SerializedName("bucket_name")
        var s3BucketVoiceNotes: String? = null,
        @SerializedName("s3_bucket_region")
        var s3BucketRegion: String? = null) {
    var demand: String? = null
        get() {
            return if (demand.isNullOrEmpty())
                return BYKEA_DOMAIN
            else
                field
        }
}