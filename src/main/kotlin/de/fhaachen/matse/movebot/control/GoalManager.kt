package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.commands.AddMovementCommand
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

// Für Wochenziele, oder Gesamtziele
object GoalManager {

    private var nextWeekReminder = LocalDateTime.now().withHour(12).withMinute(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))!!
    private var nextWeekResult: LocalDateTime = LocalDateTime.now().withHour(18).withMinute(0).with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY))!!

    fun checkGoals() {
        val currentTime = LocalDateTime.now()

        if (nextWeekReminder.isBefore(currentTime))
            handleWeekReminder()


        if (nextWeekResult.isBefore(currentTime))
            handleWeekResult()
    }

    private fun handleWeekReminder() {
        nextWeekReminder = nextWeekReminder.plusDays(7)

        val stat = StatisticsManager.getLastWeekStatistic()
        stat.sortByValue(false)

        val keyboard = inlineKeyboardFromPair(Pair("Aktivität erfassen", AddMovementCommand.command))

        ChallengerManager.challengers.forEach { challenger ->
            val message = if (stat.containsChallenger(challenger)) {
                val place = stat.statEntries.indexOfFirst { it.challenger == challenger } + 1
                val distance = stat.statEntries.first { it.challenger == challenger }.value()
                "Du hast diese Woche *$distance Kilometer* erreicht!\nDamit bist du auf *Platz $place* (für diese Woche)."
            } else "Du hast in der letzten Woche keine Bewegungen Aktivitäten!\nAchtung: Du wirst nicht in der wöchentlichen Statistik um 18:00 auftauchen!\nWillst du das wirklich?"

            try {
                ChallengeBot.sendMessage(challenger.telegramUser.id, message, keyboard).also { MessageHandler.addDeleteableMessage(it, MessageType.WEEKLY_REMINDER) }
            } catch (e: Exception) {
            }
        }
    }

    private fun handleWeekResult() {
        nextWeekResult = nextWeekResult.plusDays(7)

        val stat = StatisticsManager.getLastWeekStatistic()
        stat.sortByValue(false)
        val chart = ChartsManager.getChartPicture(stat)
        val totalDistance = stat.statEntries.sumByDouble { it.value() }.round(0)

        val messageOverview = "*Ergebnis der Woche*\n" +
                "Wir haben gemeinsam *$totalDistance Kilometer* erreicht.\n" +
                (if (totalDistance > 2020) "Es ist vollbracht! 2020km in nur einer Woche! Ich bin stolz auf euch!" else "Schaffen wir die 2020?") + "\n" +
                stat.statEntries.foldIndexed("") { index, old, entry -> "$old\n${index + 1}. *${entry.label}* (${entry.value()} km)" }

        ChallengerManager.challengers.forEach { challenger ->
            val customMessage = if (stat.containsChallenger(challenger)) {
                val place = stat.statEntries.indexOfFirst { it.challenger == challenger } + 1
                if (place <= ChallengerManager.challengers.size * 0.8)
                    "Herzlichen Glückwunsch zu *Platz $place*!"
                else
                    "Nächste Woche wird es besser! :P"
            } else "Du scheinst dich nicht zu bewegen. Lebst du noch?"

            try {
                ChallengeBot.execute(SendPhoto().setChatId(challenger.telegramUser.id.toLong())
                        .setPhoto(chart).setCaption(messageOverview + "\n\n" + customMessage).setParseMode("markdown"))
                MessageHandler.cleanupMessages(challenger.telegramUser.id.toLong(), MessageCleanupCause.WEEKLY_STAT)
            } catch (e: Exception) {
            }
        }
    }

}