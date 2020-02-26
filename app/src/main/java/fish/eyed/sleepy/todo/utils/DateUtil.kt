package fish.eyed.sleepy.todo.utils

import java.text.SimpleDateFormat
import java.util.*

object DateUtil {
    private const val DATE_PATTERN = "yyyy/MM/dd"
    private const val TIME_PATTERN = "HH:mm"
    private const val DATETIME_PATTERN = "$DATE_PATTERN $TIME_PATTERN"

    @JvmStatic
    fun formatDate(input: Date?, pattern: String = DATE_PATTERN): String? {
        return input?.let { SimpleDateFormat(pattern, Locale.JAPANESE).format(input) }
    }

    @JvmStatic
    fun formatDate(year: Int, month: Int, dayOfMonth: Int): String {
        return String.format("%04d/%02d/%02d", year, month, dayOfMonth)
    }

    @JvmStatic
    fun formatTime(input: Date?, pattern: String = TIME_PATTERN): String? {
        return input?.let { SimpleDateFormat(pattern, Locale.JAPANESE).format(input) }
    }

    @JvmStatic
    fun formatTime(hourOfDay: Int, minute: Int): String {
        return String.format("%02d:%02d", hourOfDay, minute)
    }

    @JvmStatic
    fun toCalendar(input: String?, pattern: String): Calendar? {
        return toDate(input, pattern)?.let { d ->
            Calendar.getInstance().apply { time = d }
        }
    }

    @JvmStatic
    fun toDateCalendar(input: String?): Calendar? {
        return toDate(input, DATE_PATTERN)?.let { d ->
            Calendar.getInstance().apply { time = d }
        }
    }

    @JvmStatic
    fun toTimeCalendar(input: String?): Calendar? {
        return toDate(input, TIME_PATTERN)?.let { d ->
            Calendar.getInstance().apply { time = d }
        }
    }

    @JvmStatic
    fun toDate(input: String?, pattern: String = DATE_PATTERN): Date? {
        return input?.let { SimpleDateFormat(pattern, Locale.JAPANESE).parse(input) }
    }

    @JvmStatic
    fun toDateTime(input: String?): Date? {
        return toDate(input, DATETIME_PATTERN)
    }
}
