package de.fhaachen.matse.movebot.model

import java.time.LocalDate
import java.time.LocalTime

data class Reminder(val reminderType: ReminderType,
                    val reminderTime: LocalTime) {

    var lastExecution = (if (reminderTime.isAfter(LocalTime.now())) LocalDate.now().minusDays(1) else LocalDate.now())!!

    fun isPending(): Boolean =
            lastExecution.isBefore(LocalDate.now()) && isRingingToday() && reminderTime.isBefore(LocalTime.now())

    private fun isRingingToday(): Boolean {
        if (reminderType == ReminderType.DAILY) return true

        return when (LocalDate.now().dayOfWeek.value) {
            in 1..5 -> reminderType == ReminderType.WORKING_DAYS
            in 6..7 -> reminderType == ReminderType.WEEKEND
            else -> false
        }
    }
}