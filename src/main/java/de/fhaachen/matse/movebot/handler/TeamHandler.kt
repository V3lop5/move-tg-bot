package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.TeamCreated
import de.fhaachen.matse.movebot.handler.events.TeamJoined
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Team

object TeamHandler : TeamCreated, TeamJoined {

    val teamCreatedListener = mutableListOf<TeamCreated>()
    val teamJoinedListener = mutableListOf<TeamJoined>()

    override fun onTeamCreation(challenger: Challenger, team: Team) {
        teamCreatedListener.forEach { it.onTeamCreation(challenger, team) }
    }

    override fun onTeamJoin(challenger: Challenger, team: Team) {
        teamJoinedListener.forEach { it.onTeamJoin(challenger, team) }
    }

}