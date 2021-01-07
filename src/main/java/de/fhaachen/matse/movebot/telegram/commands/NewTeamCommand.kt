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

object NewTeamCommand : ChallengerCommand("newteam", "Erstelle ein neues Team.") {
    init {
        requirements += notSuspiciousRequirement
        parameters.add(Parameter("Teamname", "Gebe einen Namen für dein neues Team ein.", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val teamname = params.first()

        if (TeamManager.teams.any { it.name.equals(teamname, true) }) {
            sendComplete(chat, "Es existiert bereits ein Team mit dem Namen $teamname. Das Team kann nicht erstellt werden.")
            return
        }

        if (TeamManager.getTeams(challenger).isNotEmpty()) {
            sendComplete(chat, "Du bist bereits in einem Team. Du kannst kein Neues erstellen.")
            return
        }

        val team = TeamManager.createTeam(teamname, challenger)
        sendComplete(chat, "Du hast erfolgreich das Team _${team.name}_ (ID ${team.teamId}) erstellt.\n\n" +
                "Andere Teilnehmer können jetzt deinem Team beitreten. Sie müssen dazu den Befehl /${JoinTeamCommand.command} verwenden und den Teamnamen _${team.name}_ angeben.")
    }
}