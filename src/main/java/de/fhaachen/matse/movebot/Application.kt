package de.fhaachen.matse.movebot

import de.fhaachen.matse.movebot.console.ConsoleEventListener
import de.fhaachen.matse.movebot.console.DataChangeEventListener
import de.fhaachen.matse.movebot.control.*
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import org.telegram.telegrambots.ApiContextInitializer
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.time.LocalDateTime
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess


val bot_tokenFile = File("bot_token")
val bot_nameFile = File("bot_name")
val botToken = if (bot_tokenFile.exists()) bot_tokenFile.readText() else ""
val botName = if (bot_nameFile.exists()) bot_nameFile.readText() else ""


var lastDataSave = LocalDateTime.now().minusSeconds(1)!!
var lastDataChange = LocalDateTime.now()!!

fun main() {
    ConsoleEventListener.register()
    DataChangeEventListener.register()
    MovePointsComplete.register()
    GoalComplete.register()

    ApiContextInitializer.init()

    val botsApi = TelegramBotsApi()

    try {
        botsApi.registerBot(ChallengeBot)
    } catch (e: TelegramApiException) {
        e.printStackTrace()
    }

    fixedRateTimer(period = 60000) {
        save()
    }

    fixedRateTimer(period = 50000) {
        ReminderManager.checkRemindersFromChallengers()
        WeeklyStatManager.checkGoals()
    }
}

fun save(force: Boolean = false) {
    if (force || lastDataChange.isAfter(lastDataSave)) {
        println("[save] Speichere Daten... ${LocalDateTime.now().prettyString()}")
        ChallengerManager.save()
        TeamManager.save()
        lastDataSave = LocalDateTime.now()
    }

}

fun exit() {
    ChallengeBot.shutdownBot()
    save(true)
    exitProcess(0)
}