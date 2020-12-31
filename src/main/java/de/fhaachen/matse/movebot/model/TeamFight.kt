package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime

data class TeamFight(
    val teamA: Int,
    val teamB: Int,
    val movementType: MovementType,
    val startTime: LocalDateTime = LocalDateTime.now()
)
