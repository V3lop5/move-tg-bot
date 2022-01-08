package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.*
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.ConfirmHandler
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object LeaderboardCommand : ChallengerCommand("leaderboard", "Rangliste je Sportart.") {

    init {
        requirements += notSuspiciousRequirement
        requirements += anyActivityRequirement

        onlyUserChat()
        parameters.add(movementTypeParameter)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.of(params[0])
        val results = ChallengerManager.challengers
            .asSequence()
            .filterNot { it.suspicious }
            .map { Pair(StatisticsManager.getSum(it, movementType), it.nickname) }
            .filter { it.first > 0 }
            .sortedByDescending { it.first }
            .mapIndexed { index, (value, nickname) -> "`${(index + 1).padStart(2)}. ${value.round(1).padStart(6)} ${movementType.unit}` ${nickname.escapeMarkdown()}" }
            .toList()

        if(results.isEmpty()) {
            sendComplete(chat, "Bisher wurden keine Aktivitäten in der Sportart ${movementType.title} ${movementType.emoji} erfasst.\nWenn du jetzt anfängst, sicherst du dir womöglich Platz 1!")
            return
        }

        sendComplete(chat, results.fold("${movementType.emoji} *Rangliste ${movementType.title}*:\n") { a, b -> "$a\n$b" })
    }
}