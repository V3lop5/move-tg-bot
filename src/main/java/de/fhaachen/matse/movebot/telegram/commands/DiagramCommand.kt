package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChartsManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.TimeInterval
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.notSuspiciousRequirement
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.LocalDateTime

object DiagramCommand : ChallengerCommand("diagram", "Zeigt den Verlauf als Diagramm an.") {

    init {
        requirements += notSuspiciousRequirement
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val dailyStat = StatisticsManager.getCompetitorStatistic(TimeInterval.DAILY)
        val weeklyStat = StatisticsManager.getCompetitorStatistic()
        val lastWeek = StatisticsManager.getLastWeekStatistic()
        val monthStat = StatisticsManager.getMonthlyStatistic()
        lastWeek.sortByValue(false)
        val myMovementStat = StatisticsManager.getMovementSplittedByType(challenger)
        sender.execute(SendPhoto().setChatId(chat.id).setPhoto(ChartsManager.getChartPicture(dailyStat)).setCaption("Vergleich aller Competitor (täglich) vom ${LocalDateTime.now().prettyDateString()}"))
        sender.execute(SendPhoto().setChatId(chat.id).setPhoto(ChartsManager.getChartPicture(weeklyStat)).setCaption("Vergleich aller Competitor (wöchentlich) vom ${LocalDateTime.now().prettyDateString()}"))
        sender.execute(SendPhoto().setChatId(chat.id).setPhoto(ChartsManager.getChartPicture(lastWeek)).setCaption("Wochenrückblick vom ${LocalDateTime.now().prettyDateString()}"))
        sender.execute(SendPhoto().setChatId(chat.id).setPhoto(ChartsManager.getChartPicture(monthStat)).setCaption("Bewegungen nach Monaten vom ${LocalDateTime.now().prettyDateString()}"))
        sender.execute(SendPhoto().setChatId(chat.id).setPhoto(ChartsManager.getChartPicture(myMovementStat)).setCaption("Deine Aufteilung der Bewegungen vom ${LocalDateTime.now().prettyDateString()}"))
        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.COMMAND_COMPLETE)
    }

}