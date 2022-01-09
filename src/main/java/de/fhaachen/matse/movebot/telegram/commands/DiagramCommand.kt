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
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File
import java.time.LocalDateTime

object DiagramCommand : ChallengerCommand("diagram", "Zeigt den Verlauf als Diagramm an.") {

    init {
        requirements += notSuspiciousRequirement
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val dailyStat = StatisticsManager.getCompetitorStatistic(TimeInterval.DAILY)
        sendDiagram(sender, chat, ChartsManager.getChartPicture(dailyStat),
            "Vergleich aller Competitor (täglich) vom ${LocalDateTime.now().prettyDateString()}")

        val weeklyStat = StatisticsManager.getCompetitorStatistic()
        sendDiagram(sender, chat, ChartsManager.getChartPicture(weeklyStat),
            "Vergleich aller Competitor (wöchentlich) vom ${LocalDateTime.now().prettyDateString()}")

        val lastWeek = StatisticsManager.getLastWeekStatistic()
        lastWeek.sortByValue(false)
        sendDiagram(sender, chat, ChartsManager.getChartPicture(lastWeek),
            "Wochenrückblick vom ${LocalDateTime.now().prettyDateString()}")

        val monthStat = StatisticsManager.getMonthlyStatistic()
        sendDiagram(sender, chat, ChartsManager.getChartPicture(monthStat),
            "Bewegungen nach Monaten vom ${LocalDateTime.now().prettyDateString()}")

        val myMovementStat = StatisticsManager.getMovementSplittedByType(challenger)
        sendDiagram(sender, chat, ChartsManager.getChartPicture(myMovementStat),
            "Deine Aufteilung der Bewegungen vom ${LocalDateTime.now().prettyDateString()}")


        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.COMMAND_COMPLETE)
    }

    private fun sendDiagram(sender: AbsSender, chat: Chat, photoFile: File, caption: String): SendPhoto {
        val request = SendPhoto()
        request.photo = InputFile(photoFile)
        request.chatId = chat.id.toString()
        request.caption = caption
        request.parseMode = "markdown"
        return request
    }
}