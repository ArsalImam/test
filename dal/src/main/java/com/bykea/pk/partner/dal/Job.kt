package com.bykea.pk.partner.dal

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DAO class for Job Requests
 *
 * @Author: Yousuf Sohail
 */
@Entity(tableName = "jobs")
data class Job(@PrimaryKey @ColumnInfo(name = "id") val id: Long,
               val state: String?,
               val booking_no: String?,
               val order_no: String?,
               val trip_id: String?,
               val trip_type: String?,
               val service_code: Int,
               val customer_id: String?,
               val creator_type: String?,
               val fare_est: Int,
               val cod_value: Int?,
               val amount: Int?,
               val voice_note: String?,
               val dt: String?,
               val priority: Int?,
               @Embedded(prefix = "pick_") val pickup: Stop?,
               @Embedded(prefix = "drop_") val dropoff: Stop?,
               @Embedded(prefix = "receiver_") val receiver: Contact?,
               @Embedded(prefix = "sender_") val sender: Contact?) {

    var isComplete: Boolean = false

}


data class Contact(
        val name: String?,
        val phone: String?,
        val address: String?
)
