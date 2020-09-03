package com.bykea.pk.partner.dal.util

import androidx.room.TypeConverter
import com.bykea.pk.partner.dal.Trips
import com.google.gson.Gson


/**
 * Created by Sibtain Raza on 8/24/2020.
 */
class Converters {
    @TypeConverter
    fun listToJson(value: List<Trips>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<Trips>::class.java).toList()
}