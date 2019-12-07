package com.bykea.pk.partner.dal.source.remote.data

import android.os.Parcelable
import com.bykea.pk.partner.dal.util.BOOKING_CURRENT_DATE_FORMAT
import com.bykea.pk.partner.dal.util.BOOKING_REQUIRED_DATE_FORMAT
import com.bykea.pk.partner.dal.util.DateUtils
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

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
        @SerializedName("tags")
        val tags: List<Tag>?
) : Parcelable {
    val formattedDate: String
        get() {
            return DateUtils.getFormattedDate(dt, BOOKING_CURRENT_DATE_FORMAT, BOOKING_REQUIRED_DATE_FORMAT)
        }
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
        val partner: Double?
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