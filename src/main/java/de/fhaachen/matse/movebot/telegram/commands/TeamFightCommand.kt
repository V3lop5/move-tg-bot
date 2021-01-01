package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.TeamManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.padByMaxValue
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object TeamFightCommand : ChallengerCommand("teamfight", "Auflistung deiner Teamfights.") {

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val fights = TeamManager.getActiveFights(challenger).groupBy { it.other }

        if (fights.isEmpty()) {
            sendComplete(chat, "Du nimmst an keinen Team Wettkämpfen teil. Starte einen Teamkampf: /${NewTeamFightCommand.command}")
            return
        }

        val message = fights.map { (other, fights) ->
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
                    "\nEs steht *${fights.count { it.isLeading}} zu ${fights.count { !it.isLeading && !it.isEqual }}*" +
                    if (fights.any { it.isEqual }) " (Gleichstand bei *${fights.count { it.isEqual }} Sportart(en)*)" else ""
        }.joinToString("\n\n", "*Deine Teamkämpfe:*\n\n")

        sendComplete(chat, message)
    }

}