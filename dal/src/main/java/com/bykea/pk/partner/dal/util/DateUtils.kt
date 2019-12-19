package com.bykea.pk.partner.dal.util

import org.apache.commons.lang3.StringUtils
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * utility class for date object
 *
 * @author ArsalImam
 */
object DateUtils {

    /**
     * this method will parse string date object to the mentioned [outFormat]
     *
     * @param dateStr date which needs to convert
     * @param inFormat existing format of the date
     * @param outFormat expected format for date
     */
    fun getFormattedDate(dateStr: String, inFormat: String, outFormat: String): String {
        val inFormatter = SimpleDateFormat(inFormat)
        val outFormatter = SimpleDateFormat(outFormat)

        var date: Date? = null
        try {
            date = inFormatter.parse(dateStr)
        } catch (e: ParseException) {
            e.printStackTrace()
            return StringUtils.EMPTY
        }

        return outFormatter.format(date)
    }
}