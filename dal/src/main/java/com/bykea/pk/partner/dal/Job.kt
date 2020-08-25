package com.bykea.pk.partner.dal

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.bykea.pk.partner.dal.util.NEGATIVE_DIGIT_ONE

/**
 * DAO class for Job Requests
 *
 * @Author: Yousuf Sohail
 */
@Entity(tableName = "jobs")
data class Job(@PrimaryKey @ColumnInfo(name = "id") val id: Long,
               val state: String? = null,
               val booking_no: String? = null,
               val order_no: String? = null,
               val trip_id: String? = null,
               val trip_type: String? = null,
               var service_code: Int? = null,
               val customer_id: String? = null,
               val customer_name: String? = null,
               val creator_type: String? = null,
               val fare_est: Int? = null,
               val fare_est_str: String? = null,
               val service_icon: String? = null,
               val cod_value: Int? = null,
               val amount: Int? = null,
               val voice_note: String? = null,
               val dt: String? = null,
               @Embedded(prefix = "rules_") val rules: Rules? = null,
               @Embedded(prefix = "pick_") val pickup: Stop? = null,
               @Embedded(prefix = "drop_") val dropoff: Stop? = null,
               @Embedded(prefix = "receiver_") val receiver: Contact? = null,
               @Embedded(prefix = "sender_") val sender: Contact? = null,
               val trips: List<Trips>? = null) {
    var isComplete: Boolean = false
}


data class Contact(
        val name: String?,
        val phone: String?,
        val address: String?
)

data class Rules(
        val priority: Int?
)

data class Trips(
        val display_tag: String?,
        val zone_ur: String?
)
