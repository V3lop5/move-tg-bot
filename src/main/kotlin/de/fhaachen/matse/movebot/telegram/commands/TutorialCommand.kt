package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender

object TutorialCommand : ChallengerCommand("tutorial", "Detaillierte Beschreibung der Funktionen dieses Bots.") {

    val tutorial = mutableListOf<String>()

    init {
        onlyUserChat()

        parameters += object : Parameter("Seite", "Gebe die Seite des Tutorials ein. (Seite 1-${tutorial.size})", optional = true) {
            override fun isValueAllowed(value: String) = value.matches(Regex("^[0-9]*$")) && value.toInt() > 0 && value.toInt() <= tutorial.size
        }

        tutorial += "Test *Test* \n _Test_ /test `Test`"
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.TUTORIAL_COMMAND)
        val page = (params.firstOrNull()?.toInt()?:1)

        val message = "($page/${tutorial.size})      *Tutorial*\n\n${tutorial[page-1]}"
        sendMessage(chat, message, inlineKeyboardFromPair(listOf("Zurück" to "$command ${page - 1}", "Weiter" to "$command ${page + 1}").filterIndexed {idx, _ -> (page == 1 && idx != 0) || (page == tutorial.size && idx != 1) } ))
            .also { MessageHandler.addDeleteableMessage(it, MessageType.TUTORIAL) }
    }
}