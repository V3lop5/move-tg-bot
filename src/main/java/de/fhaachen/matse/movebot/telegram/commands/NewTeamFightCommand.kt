package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.TeamManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.model.Plan
import de.fhaachen.matse.movebot.model.TeamMember
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object NewTeamFightCommand : ChallengerCommand("newteamfight", "Trete in einem Kampf mit einem anderen Team.") {
    init {
        requirements += notSuspiciousRequirement
        parameters.add(movementTypeParameter)
        parameters.add(Parameter("Teamname", "Gebe den Namen des Teams ein, mit dem sich dein Team messen soll.", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.of(params[0])
        val teamname = params[1]

        val own = TeamManager.teams.singleOrNull { it.members.any { it.challengerId == challenger.telegramUser.id } }

        if (own == null) {
            sendComplete(chat, "Du bist in keinem oder mehreren Teams. Du kannst deshalb kein Wettkampf starten. Erstelle zuerst ein Team (/${NewTeamCommand.command}) oder trete einem Team bei (/${JoinTeamCommand.command}).")
            return
        }

        val other = TeamManager.teams.find { it.name.equals(teamname, true) }

        if (other == null) {
            sendComplete(chat, "Es existiert kein Team mit dem Namen $teamname. Du kannst deshalb kein Wettkampf starten.")
            return
        }


        if (own.teamId == other.teamId) {
            sendMessage(chat, "Du kannst keinen Wettkampf mit dem eigenen Team starten.")
            return
        }

        if (TeamManager.hasFight(own, other, movementType)) {
            sendComplete(chat, "Es gibt bereits einen Wettkampf der Sportart *${movementType.title}* zwischen den Teams _${own.name.escapeMarkdown()}_ und _${other.name.escapeMarkdown()}_.")
            return
        }

        TeamManager.onFightRequest(own, other, movementType)
        own.members.forEach {
            ChallengeBot.sendMessage(it.challengerId, "Anfrage für Teamkampf verschickt!\n" +
                    "${challenger.nickname.escapeMarkdown()} hat im Namen deines Teams _${own.name.escapeMarkdown()}_ eine Herausforderung zum Kampf in der Sportart ${movementType.title} an das Team _${other.name.escapeMarkdown()}_ verschickt.\n" +
                    "Ob sie die Herausforderung annehmen werden?")
        }
    }
}