package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.TeamManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.model.Plan
import de.fhaachen.matse.movebot.model.TeamMember
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object JoinTeamCommand : ChallengerCommand("jointeam", "Trete einem Team bei.") {
    init {
        requirements += notSuspiciousRequirement

        parameters.add(Parameter("Teamname", "Gebe einen Namen des Teams ein, dem du beitreten möchtest.", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val teamname = params.first()

        if (TeamManager.teams.none { it.name.equals(teamname, true) }) {
            sendComplete(chat, "Es existiert kein ein Team mit dem Namen $teamname. Du kannst diesem Team nicht beitreten.")
            return
        }

        TeamManager.onJoinRequest(challenger, teamname)
    }
}