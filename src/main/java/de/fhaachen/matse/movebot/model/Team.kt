package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.control.ChallengerManager

data class Team(
    val teamId: Int,
    val name: String,
    val members: MutableList<TeamMember> = mutableListOf()
){
    fun getChallengers() = members.mapNotNull { member -> ChallengerManager.challengers.find { it.telegramUser.id == member.challengerId } }
}
