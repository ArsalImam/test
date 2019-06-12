package com.bykea.pk.partner.dal

import androidx.room.*

/**
 * DAO class for booking
 *
 * @Author: Yousuf Sohail
 */
@Entity(tableName = "bookings")
data class Booking(@PrimaryKey @ColumnInfo(name = "id") val id: Long,
                   val state: String?,
                   val booking_no: String?,
                   val order_no: String?,
                   val trip_id: String?,
                   val trip_type: String?,
                   val service_code: Int,
                   val customer_id: String?,
                   val creator_type: String?,
                   val fare_est: Double,
                   val amount: Double?,
                   val voice_note: String?,
                   val dt: String,
                   @Embedded(prefix = "pick_") val pickup: Stop?,
                   @Embedded(prefix = "drop_") val dropoff: Stop?,
                   @Embedded(prefix = "receiver_") val receiver: Contact?,
                   @Embedded(prefix = "sender_") val sender: Contact?) {

    @Ignore
    var serviceIcon: String = "bhejdo_no_caption"
        get() = when (service_code) {
            22 -> "bhejdo_no_caption"
            21 -> "bhejdo_no_caption"
            else -> "bhejdo_no_caption"
        }

}
