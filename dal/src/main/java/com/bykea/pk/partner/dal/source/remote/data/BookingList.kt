package com.bykea.pk.partner.dal.source.remote.data


import android.os.Parcelable
import com.bykea.pk.partner.dal.R
import com.bykea.pk.partner.dal.util.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils

@Parcelize
data class BookingList(
        @SerializedName("service")
        val service: Service?,
        @SerializedName("booking_code")
        val bookingCode: String?,
        @SerializedName("booking_id")
        val bookingId: String?,
        @SerializedName("dt")
        val dt: String?,
        @SerializedName("fare")
        val fare: String?,
        @SerializedName("cancel_by")
        val cancel_by: String?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("tags")
        val tags: ArrayList<Tag>
) : Parcelable {

    val priorTag: String
        get() {
            if (tags.size > DIGIT_ZERO) {
                tags.sortByDescending { it.priority }
                if (tags[DIGIT_ZERO]?.icon != null) {
                    return tags[DIGIT_ZERO].icon!!
                }
            }
            return StringUtils.EMPTY
        }

    val manipulatedAmount: String
        get() {
            when (status) {
//                 -> {
//                    return if (cancel_fee == null) {
//                        LABEL_NOT_AVAILABLE
//                    } else {
//                        cancel_fee
//                    }
//                }
                AvailableTripStatus.STATUS_CANCELLED,
                AvailableTripStatus.STATUS_FEEDBACK,
                AvailableTripStatus.STATUS_COMPLETED ->
                    return if (fare == null) {
                        LABEL_NOT_AVAILABLE
                    } else {
                        fare.toString()
                    }
                AvailableTripStatus.STATUS_MISSED -> StringUtils.EMPTY
            }
            return StringUtils.EMPTY
        }
    val statusColor: Int
        get() {
            when (status) {
                AvailableTripStatus.STATUS_CANCELLED,
                AvailableTripStatus.STATUS_MISSED -> return R.color.color_error_dal
                else -> return R.color.colorAccent_dal
            }
        }
    val showAmount: Boolean
        get() {
            when (status) {
                AvailableTripStatus.STATUS_FEEDBACK,
                AvailableTripStatus.STATUS_CANCELLED,
                AvailableTripStatus.STATUS_COMPLETED -> return true
            }
            return false
        }
    val formatedStatus: String
        get() {
            when (status) {
                AvailableTripStatus.STATUS_FEEDBACK,
                AvailableTripStatus.STATUS_COMPLETED -> return AvailableTripStatus.STATUS_COMPLETED.capitalize()
                AvailableTripStatus.STATUS_CANCELLED -> return "${cancel_by?.capitalize()} ${AvailableTripStatus.STATUS_CANCELLED.capitalize()}"
            }
            return StringUtils.EMPTY
        }
    val formatedDate: String
        get() {
            return DateUtils.getFormattedDate(dt.toString(), BOOKING_CURRENT_DATE_FORMAT, BOOKING_LIST_REQUIRED_DATE_FORMAT)
        }

}

//@Parcelize
//data class InvoiceBookingList(
//        @SerializedName("fare")
//        val fare: Int? = 0
//) : Parcelable