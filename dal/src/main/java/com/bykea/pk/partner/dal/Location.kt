package com.bykea.pk.partner.dal

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "LocationHistory")
data class Location(@PrimaryKey val time: Long, val lat: Double, val lng: Double)