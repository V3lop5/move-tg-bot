package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime
import java.time.temporal.WeekFields
import java.util.*

enum class TimeInterval {

    DAILY {
        override val todaysMax: Int
            get() = LocalDateTime.now().dayOfYear

        override fun groupBy(movement: Movement): Int = movement.datetime.dayOfYear

    },
    WEEKLY {
        override val todaysMax: Int
            get() = LocalDateTime.now().get(WeekFields.of(Locale.GERMAN).weekOfYear())

        override fun groupBy(movement: Movement): Int = movement.datetime.get(WeekFields.of(Locale.GERMAN).weekOfYear())
    },
    MONTHLY {
        override val todaysMax: Int
            get() = LocalDateTime.now().monthValue

        override fun groupBy(movement: Movement): Int = movement.datetime.monthValue
    };

    abstract val todaysMax: Int
    abstract fun groupBy(movement: Movement): Int
}