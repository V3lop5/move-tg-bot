package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object ChallengeCommand : ChallengerCommand("challenge", "Du willst wissen, wie es um dich steht? Kannst du das Ziel deine Ziele noch erreichen? Die Infos gibts hier!") {

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val message = "Du hast bereits *${StatisticsManager.getPoints(challenger)} Punkte* erzielt!\n" +
                "Davon _${StatisticsManager.getCurrentMonthPoints(challenger)} Punkte_ im diesem Monat.\n\n" +
                "Deine persönlichen Ziele:\n${challenger.goals.map { (type, goal) -> 
                    StatisticsManager.getSum(challenger, type).let { "`${(minOf(it/goal,1.0) * 100).toInt().padStart(3)}` % ${type.emoji}   $it von *$goal ${type.unit}* ${type.title} " } 
                }.joinToString(prefix = "- ", separator = "\n- ")}\n\n" +
                "Nach Bewegungstyp:\n${MovementType.values().map { Triple(it, StatisticsManager.getSum(challenger, it), StatisticsManager.getPoints(challenger, it)) }.filter { it.second > 0 }
                    .map { (type, sum, points) -> "${type.emoji} `${sum.padStart(4)}` ${type.unit} *${type.name.escapeMarkdown()}* ($points Pkt.)" }.joinToString(prefix = "", separator = "\n")}\n\n" +
                "Deine letzten Aktivitäten:\n${
                challenger.movements.sortedBy { it.datetime }.takeLast(5).joinToString(prefix = "- ", separator = "\n- ") { "${it.value} ${it.type.unit} ${it.type.name.escapeMarkdown()} (${getRelativeTimeSpan(it.datetime)})" }}\n\n" +
                "Wo sind die anderen? /${CompetitorCommand.command}"
        sendComplete(chat, message)
    }
}