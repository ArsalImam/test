package sa.ai.keeptruckin.utils

import android.content.res.Resources
import sa.ai.keeptruckin.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * [author] by `Arsal Imam`
 * [created] on 5/18/2020
 *
 * utility class to manage time resources
 */
object TimeUtils {
    private const val FORMAT_DATE: String = "yyyy-MM-dd hh:mm:ss"
    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS

    /**
     * this method will return the [Date] object with current date/time
     */
    private fun currentDate(): Date {
        val calendar = Calendar.getInstance()
        return calendar.time
    }

    /**
     * will calculate the time difference from current date to the date specified
     *
     * [formattedTime] from which difference needs to calculate
     * [resources] to obtain string literals
     */
    fun getTimeAgo(resources: Resources, formattedTime: String): String {
        var time = parseStringToDate(formattedTime).time
        if (time < 1000000000000L) {
            time *= 1000
        }

        val now = currentDate().time
        if (time > now || time <= 0) {
            return resources.getString(R.string.in_future)
        }

        val diff = now - time
        return when {
            diff < MINUTE_MILLIS -> resources.getString(R.string.moments_ago)
            diff < 2 * MINUTE_MILLIS -> resources.getString(R.string.a_minutes_ago)
            diff < 60 * MINUTE_MILLIS -> "${diff / MINUTE_MILLIS} ${resources.getString(R.string.minutes_ago)}"
            diff < 2 * HOUR_MILLIS -> resources.getString(R.string.hours_ago)
            diff < 24 * HOUR_MILLIS -> "${diff / HOUR_MILLIS} ${resources.getString(R.string.a_hours_ago)}"
            diff < 48 * HOUR_MILLIS -> resources.getString(R.string.yesterday)
            else -> "${diff / DAY_MILLIS} ${resources.getString(R.string.days_ago)}"
        }
    }

    /**
     * parse string date to [Date] object
     */
    private fun parseStringToDate(formattedTime: String): Date {
        val format: DateFormat = SimpleDateFormat(FORMAT_DATE, Locale.ENGLISH)
        return format.parse(formattedTime)
    }
}