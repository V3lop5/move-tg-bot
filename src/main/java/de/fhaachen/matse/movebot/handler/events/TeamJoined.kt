package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Reminder
import de.fhaachen.matse.movebot.model.Team

interface TeamJoined {

    fun onTeamJoin(challenger: Challenger, team: Team)
}