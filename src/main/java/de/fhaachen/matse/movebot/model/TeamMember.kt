package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import java.time.LocalDateTime

data class TeamMember(
    val challengerId: Long,
    val jointime: LocalDateTime
) {

    fun getSum(movementType: MovementType, after: LocalDateTime): Double {
        val c = ChallengerManager.findChallenger(challengerId) ?: return 0.0
        if (c.suspicious) return 0.0
        return StatisticsManager.getSum(c, movementType, maxOf(jointime, after))
    }
}
