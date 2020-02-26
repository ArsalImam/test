package com.bykea.pk.partner.dal.source.remote.data

import android.os.Parcelable
import com.bykea.pk.partner.dal.util.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import org.apache.commons.lang3.StringUtils

/**
 * response model for booking detail
 * @author ArsalImam
 */
@Parcelize
data class BookingDetail(
        @SerializedName("booking_code")
        val bookingCode: String?,
        @SerializedName("booking_id")
        val bookingId: String?,
        @SerializedName("dt")
        val dt: String = "",
        @SerializedName("customer")
        val customer: Customer?,
        @SerializedName("dropoff")
        val dropoff: Dropoff?,
        @SerializedName("invoice")
        var invoice: ArrayList<Invoice>?,
        @SerializedName("partner")
        val partner: Partner?,
        @SerializedName("pickup")
        val pickup: Pickup?,
        @SerializedName("rate")
        val rate: Rate?,
        @SerializedName("receiver")
        val `receiver`: Receiver?,
        @SerializedName("sender")
        val sender: Sender?,
        @SerializedName("service")
        val service: Service?,
        @SerializedName("status")
        val status: String?,
        @SerializedName("cancel_by")
        val cancelBy: String?,
        @SerializedName("tags")
        val tags: ArrayList<Tag>?
) : Parcelable {
    /**
     * this property will share the latest tag by the basis of priority
     */
    val priorTag: String
        get() {
            if (tags?.size!! > DIGIT_ZERO) {
                tags?.sortByDescending { it.priority }
                if (tags[DIGIT_ZERO]?.icon != null) {
                    return tags[DIGIT_ZERO].icon!!
                }
            }
            return StringUtils.EMPTY
        }

    val formattedDate: String
        get() {
            return DateUtils.getFormattedDate(dt, BOOKING_CURRENT_DATE_FORMAT, BOOKING_LIST_REQUIRED_DATE_FORMAT)
        }

//    val cancelBy: String
//        get() {
//            tags?.forEach {
//                when {
//                    it?.name == CancelByStatus.CANCEL_BY_ADMIN -> return RolesByName.CANCEL_BY_ADMIN
//                    it?.name == CancelByStatus.CANCEL_BY_PARTNER -> return RolesByName.CANCEL_BY_PARTNER
//                    it?.name == CancelByStatus.CANCEL_BY_CUSTOMER -> return RolesByName.CANCEL_BY_CUSTOMER
//                }
//            }
//            return org.apache.commons.lang3.StringUtils.EMPTY
//        }
}

@Parcelize
data class Tag(
        @SerializedName("id")
        val id: String?,
        @SerializedName("dt")
        val dt: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("description")
        val description: String?,
        @SerializedName("priority")
        val priority: String?,
        @SerializedName("icon")
        val icon: String?,
        @SerializedName("dtu")
        val dtu: String?
) : Parcelable

@Parcelize
data class Customer(
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?
) : Parcelable

@Parcelize
data class Dropoff(
        @SerializedName("address")
        val address: String?,
        @SerializedName("lat")
        val lat: String?,
        @SerializedName("lng")
        val lng: String?,
        @SerializedName("zone_en")
        val zoneEn: String?,
        @SerializedName("zone_ur")
        val zoneUr: String?
) : Parcelable

@Parcelize
data class Invoice(
        @SerializedName("bold")
        val bold: Boolean?,
        @SerializedName("separator")
        val separator: String?,
        @SerializedName("title")
        val title: String?,
        @SerializedName("title_en")
        val titleEn: String?,
        @SerializedName("title_ur")
        val titleUr: String?,
        @SerializedName("value")
        val value: Float?,
        var viewType: String? = null
) : Parcelable

@Parcelize
data class Partner(
        @SerializedName("id")
        val id: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("plate")
        val plate: String?
) : Parcelable

@Parcelize
data class Pickup(
        @SerializedName("address")
        val address: String?,
        @SerializedName("lat")
        val lat: String?,
        @SerializedName("lng")
        val lng: String?,
        @SerializedName("zone_en")
        val zoneEn: String?,
        @SerializedName("zone_ur")
        val zoneUr: String?
) : Parcelable

@Parcelize
data class Rate(
        @SerializedName("customer")
        val customer: Double?,
        @SerializedName("partner")
        val partner: Double?,
        @SerializedName("driver_feedback")
        val driverFeedback: ArrayList<String>?
) : Parcelable

@Parcelize
data class Receiver(
        @SerializedName("address")
        val address: String?,
        @SerializedName("name")
        val name: String?,
        @SerializedName("phone")
        val phone: String?
) : Parcelable

@Parcelize
data class Sender(
        @SerializedName("address")
        val address: String?
) : Parcelable

@Parcelize
data class Service(
        @SerializedName("code")
        val code: Int?,
        @SerializedName("name")
        val name: String?
) : Parcelable