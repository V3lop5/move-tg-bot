package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.TeamManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.padByMaxValue
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.notSuspiciousRequirement
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object TeamFightCommand : ChallengerCommand("teamfight", "Auflistung deiner Teamfights.") {

    init {
        requirements += notSuspiciousRequirement
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val fights = TeamManager.getActiveFights(challenger).groupBy { it.other }

        if (fights.isEmpty()) {
            sendComplete(chat, "Du nimmst an keinen Team Wettkämpfen teil. Starte einen Teamkampf: /${NewTeamFightCommand.command}")
            return
        }

        val message = fights.map { (other, fights) ->
            val maxOwnValue = fights.map { it.ownValue }.maxOrNull()?:0
            val maxOtherValue = fights.map { it.otherValue }.maxOrNull()?:0

            val ownTotalPoints = fights.sumByDouble { it.ownPoints }.round(2)
            val otherTotalPoints = fights.sumByDouble { it.otherPoints }.round(2)

            fights.first().own.name + " \uD83C\uDD9A " + other.name + "\n" +
            fights.joinToString("\n") {
                "${getCircle(it.ownPoints, it.otherPoints)} ${it.movementType.emoji}" +
                    " `${it.ownValue.padByMaxValue(maxOwnValue)} vs ${it.otherValue.padByMaxValue(maxOtherValue)}`" +
                    " ${it.movementType.unit} ${it.movementType.title}" } +
                    "\nEs steht *${fights.count { it.isLeading}} zu ${fights.count { !it.isLeading && !it.isEqual }}*" +
                    (if (fights.any { it.isEqual }) " (Gleichstand bei *${fights.count { it.isEqual }} Sportart(en)*)" else "") +
                    // Challenge Punkte Summe
                    "\n${getCircle(ownTotalPoints, otherTotalPoints)} \uD83D\uDCCA `${ownTotalPoints.padStart(4)} vs ${otherTotalPoints.padStart(4)}` Challenge Punkte"
        }.joinToString("\n\n", "*Deine Teamkämpfe:*\n\n")

        sendComplete(chat, message)
    }

    private fun getCircle(ownValue: Double, otherValue: Double) = when {
        ownValue == otherValue -> "⚪️"
        ownValue > otherValue -> "\uD83D\uDFE2"
        else -> "\uD83D\uDD34"
    }

}