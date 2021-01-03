package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object CompetitorCommand : ChallengerCommand("competitor", "Hier gibt es die Statistiken der Mitstreiter zu sehen!") {

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val results = ChallengerManager.challengers
                .map { Pair(StatisticsManager.getPoints(it), it.nickname) }
                .sortedByDescending { it.first }
                .mapIndexed { index, (points, nickname) -> "`${(index + 1).padStart(2)}. ${points.round(1).padStart(6)} Pkt.` ${nickname.escapeMarkdown()}" }
        sendComplete(chat, results.fold("Übersicht aller Teilnehmer:\n") { a, b -> "$a\n$b" })
    }

}