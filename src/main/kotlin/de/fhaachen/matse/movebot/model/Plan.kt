package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime

data class Plan(val keyword: String,
                val movementType: MovementType,
                val distance: Double,
                val description: String) {
    fun toMovement(datetime: LocalDateTime = LocalDateTime.now()): Movement {
        return Movement(datetime, movementType, distance)
    }
}