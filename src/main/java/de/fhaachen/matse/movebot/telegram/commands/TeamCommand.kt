package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.TeamManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.padByMaxValue
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object TeamCommand : ChallengerCommand("team", "Zeigt die Übersicht für ein Team an") {
    init {
        parameters.add(Parameter("Teamname", "Gebe einen Namen des Teams ein, welches du dir angucken möchtest.", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val teamname = params.first()

        val team = TeamManager.teams.find { it.name.equals(teamname, true) }

        if (team == null) {
            sendComplete(chat, "Es existiert kein ein Team mit dem Namen $teamname. Du kannst dir dieses Team nicht angucken.")
            return
        }

        val fights = TeamManager.getActiveFights(team).groupBy { it.other }

        var message = "Informationen über Team _${team.name}_:\n\n"

        message += team.members.map {
            "${ChallengerManager.findChallenger(it.challengerId)?.nickname ?: "Unknown"} (${getRelativeTimeSpan(it.jointime)} beigetreten)"
        }.joinToString("\n- ", prefix = "Mitglieder:\n- ")

        message += fights.map { (other, fights) ->
            val maxOwnValue = fights.map { it.ownValue }.max()?:0
            val maxOtherValue = fights.map { it.ownValue }.max()?:0
            fights.first().own.name + " \uD83C\uDD9A " + other.name + "\n" +
                    fights.joinToString("\n") {
                        "${when {
                            it.isEqual -> "⚪️"
                            it.isLeading -> "\uD83D\uDFE2"
                            else -> "\uD83D\uDD34"
                        }} ${it.movementType.emoji}" +
                                " `${it.ownValue.padByMaxValue(maxOwnValue)} vs ${it.otherValue.padByMaxValue(maxOtherValue)}`" +
                                " ${it.movementType.unit} ${it.movementType.title}" } +
                    "\nEs steht *${fights.count { it.isLeading || it.isEqual }} zu ${fights.count { !it.isLeading }}*"
        }.joinToString("\n\n")

        sendComplete(chat, message)
    }
}