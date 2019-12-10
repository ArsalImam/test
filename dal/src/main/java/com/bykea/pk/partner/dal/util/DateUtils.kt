package com.bykea.pk.partner.dal.util

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {


    fun getFormattedDate(dateStr: String, inFormat: String, outFormat: String): String {
        val inFormatter = SimpleDateFormat(inFormat)
        val outFormatter = SimpleDateFormat(outFormat)

        var date: Date? = null
        try {
            date = inFormatter.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            return ""
        }

        return outFormatter.format(date)
    }
}