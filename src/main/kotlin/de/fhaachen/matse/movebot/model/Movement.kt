package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime

data class Movement(val datetime: LocalDateTime,
                    val type: MovementType,
                    val value: Double) {

    val points: Double
        get() = (value * type.pointsPerUnit)
}