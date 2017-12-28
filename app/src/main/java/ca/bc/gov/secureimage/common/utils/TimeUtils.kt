package ca.bc.gov.secureimage.common.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Aidan Laing on 2017-12-27.
 *
 */
object TimeUtils {

    /**
     * Turns timestamp into a human readable time such as 10 mins ago or just now
     */
    fun getReadableTime(
            timestamp: Long,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
            format: String = "MMM d, yyyy",
            suffix: String = "ago"
    ): String {

        // Time units in milliseconds
        val oneSecond = 1000L
        val oneMinute = 60000L
        val oneHour = 3600000L
        val oneDay = 86400000L
        val oneWeek = 604800000L

        val timeDifference = System.currentTimeMillis() - timestamp
        return when {
            timeDifference < oneSecond -> "just now"
            timeDifference < oneMinute -> getTimeString(timeDifference / oneSecond, "sec", suffix)
            timeDifference < oneHour -> getTimeString(timeDifference / oneMinute, "min", suffix)
            timeDifference < oneDay -> getTimeString(timeDifference / oneHour, "hour", suffix)
            timeDifference < oneWeek -> getTimeString(timeDifference / oneDay, "day", suffix)
            else -> getDateString(timestamp, locale, timeZone, format)
        }
    }

    /**
     * Creates plural version of time unit if time is not equal to 1
     * Appends suffix at end of string
     */
    fun getTimeString(timeValue: Long, timeUnit: String, suffix: String = "ago"): String {
        var timeString = "$timeValue $timeUnit"
        if (timeValue != 1L) timeString += "s"
        return "$timeString $suffix"
    }

    /**
     * Converts timestamp into specified date format
     */
    fun getDateString(
            timestamp: Long,
            locale: Locale = Locale.getDefault(),
            timeZone: TimeZone = TimeZone.getDefault(),
            format: String = "MMM d, yyyy"
    ): String {
        val date = Date(timestamp)
        val dateFormat = SimpleDateFormat(format, locale)
        dateFormat.timeZone = timeZone
        return dateFormat.format(date)
    }

}