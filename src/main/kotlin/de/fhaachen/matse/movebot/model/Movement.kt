package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime

data class Movement(val datetime: LocalDateTime,
                    val type: MovementType,
                    val distance: Double)