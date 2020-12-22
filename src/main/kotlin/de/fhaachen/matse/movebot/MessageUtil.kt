package de.fhaachen.matse.movebot

import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

fun getRelativeTimeSpan(time: LocalDateTime?): String {
    if (time == null || time == LocalDateTime.MIN) return "nie"
    return when (val daysBetween = ChronoUnit.DAYS.between(time.toLocalDate(), LocalDate.now())) {
        0L -> getRelativeTimeSpan(time.toLocalTime())
        1L -> time.format(DateTimeFormatter.ofPattern("'gestern um 'HH:mm"))
        2L -> time.format(DateTimeFormatter.ofPattern("'vorgestern um 'HH:mm"))
        in 3..6 -> "vor $daysBetween Tagen"
        else -> time.format(DateTimeFormatter.ofPattern("'am 'dd.MM."))
    }
}

fun getRelativeTimeSpan(time: LocalTime?): String {
    if (time == null) return "nie"
    val d = Duration.between(time, LocalTime.now())
    return when (d.seconds) {
        in 1..5 -> "vor wenigen Sekunden"
        in 5..55 -> "vor ${d.seconds} Sekunden"
        in 55..90 -> "vor einer Minute"
        in 90..60 * 5 -> "vor wenigen Minuten"
        in 60 * 5 until 60 * 60 -> "vor ${d.toMinutes()} Minuten"
        in 60 * 60 until 60 * 60 * 2 -> "vor einer Stunde"
        in 60 * 60 * 2..60 * 60 * 6 -> "vor ${d.toHours()} Stunden"
        else -> time.format(DateTimeFormatter.ofPattern("'um 'HH:mm"))
    }
}


fun String?.escapeMarkdown(): String {
    return this?.replace("_", "\\_")?.replace("*", "\\*") ?: ""
}
