package de.fhaachen.matse.movebot.model

import java.time.LocalDateTime

data class TeamFightStatistic(
    val own: Team,
    val other: Team,
    val movementType: MovementType,
    val startTime: LocalDateTime
) {
    val ownValue = own.members.map { it.getSum(movementType, startTime) }.sum().toInt()
    val otherValue = other.members.map { it.getSum(movementType, startTime) }.sum().toInt()

    val isLeading = ownValue > otherValue
    val isEqual = ownValue == otherValue
}
