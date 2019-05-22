package com.bykea.pk.partner.dal

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookings")
data class Booking(
        @PrimaryKey @ColumnInfo(name = "id") val bookingId: String,
        val name: String,
        val description: String
)
