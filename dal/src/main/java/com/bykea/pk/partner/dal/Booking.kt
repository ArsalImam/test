package com.bykea.pk.partner.dal

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * DAO class for booking
 *
 * @Author: Yousuf Sohail
 */
@Entity(tableName = "bookings")
data class Booking(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long,
        val service_code: Int,
        val fare: Double,
        val collectible: Double,
        @Embedded(prefix = "pick_") val pick: Stop,
        @Embedded(prefix = "drop_") val drop: Stop,
        val voice_note: String?
)
