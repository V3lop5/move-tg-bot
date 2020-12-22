package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object ChallengeCommand : ChallengerCommand("challenge", "Du willst wissen, wie es um dich steht? Kannst du das Ziel der Challenge noch erreichen? Die Infos gibts hier!") {

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val message = "Du hast bereits *${StatisticsManager.getDistance(challenger)} Kilometer* geschafft!\n\n" +
                "Diesen Monat hast du ${StatisticsManager.getCurrentMonthDistance(challenger)} Kilometer.\n\n" +
                "Nach Sportart:\n${MovementType.values().map { Pair(it, StatisticsManager.getDistance(challenger, it)) }.filter { it.second > 0 }.joinToString(prefix = "- ", separator = "\n- ")}\n\n" +
                "Deine letzten Aktivitäten:\n${
                challenger.movements.sortedBy { it.datetime }.takeLast(5).joinToString(prefix = "- ", separator = "\n- ") { "${it.type.name} ${it.distance} km (${getRelativeTimeSpan(it.datetime)})" }}\n\n" +
                "Wo sind die anderen? /${CompetitorCommand.command}"
        sendComplete(chat, message)
    }
}