package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.round
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
        onlyUserChat()
        parameters.add(movementTypeParameter)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.of(params[0])
        val results = ChallengerManager.challengers
            .map { Pair(StatisticsManager.getPoints(it, movementType), it.nickname) }
            .sortedByDescending { it.first }
            .mapIndexed { index, it -> "`${(index + 1).padStart(2)}. ${it.first.round(1).padStart(6)} ${movementType.unit}` ${it.second}" }
        sendComplete(chat, results.fold("${movementType.title} Rangliste:\n") { a, b -> "$a\n$b" })

    }
}