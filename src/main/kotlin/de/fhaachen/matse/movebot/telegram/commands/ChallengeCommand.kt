package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object ChallengeCommand : ChallengerCommand("challenge", "Du willst wissen, wie es um dich steht? Kannst du das Ziel deine Ziele noch erreichen? Die Infos gibts hier!") {

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val message = "Du hast bereits *${StatisticsManager.getPoints(challenger)} Punkte* erzielt!\n" +
                "Diesen Monat hast du bereits ${StatisticsManager.getCurrentMonthPoints(challenger)} Punkte.\n\n" +
                "Deine Ziele:\n${challenger.goals.map { (type, goal) -> 
                    StatisticsManager.getSum(challenger, type).let { "${type.name} $it ${type.unit} / *$goal ${type.unit}* (${(minOf(it/goal,1.0) * 100).toInt()} %)" } 
                }.joinToString(prefix = "- ", separator = "\n- ")}\n\n" +
                "Nach Bewegungstyp:\n${MovementType.values().map { Pair(it, StatisticsManager.getSum(challenger, it)) }.filter { it.second > 0 }
                    .map { (type, sum) -> "${type.emoji} $sum ${type.unit} *${type.name}* " }.joinToString(prefix = "- ", separator = "\n- ")}\n\n" +
                "Deine letzten Aktivitäten:\n${
                challenger.movements.sortedBy { it.datetime }.takeLast(5).joinToString(prefix = "- ", separator = "\n- ") { "${it.type.name} ${it.value} ${it.type.unit} (${getRelativeTimeSpan(it.datetime)})" }}\n\n" +
                "Wo sind die anderen? /${CompetitorCommand.command}"
        sendComplete(chat, message)
    }
}