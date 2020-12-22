package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.RequestHandler
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

open class Parameter(
        val name: String,
        val helpText: String = "*$name* fehlt",
        val optional: Boolean = false,
        val singleWord: Boolean = true
) {

    open fun isValueAllowed(value: String) = true

    open fun request(chat: Chat, command: Command, parsedValues: List<String>, unparsedValues: List<String>, wrongInput: String? = null) {
        RequestHandler.requestMessage(chat, sendHelpMessage(chat.id, wrongInput)) { sender: AbsSender, message ->
            if (!message.hasText()) {
                ChallengeBot.sendMessage(chat.id, "Bitte schicke eine Textnachricht!")
                request(chat, command, parsedValues, unparsedValues, wrongInput)
                return@requestMessage
            }

            val paramValue = if (singleWord) listOf(message.text.replace(" ", "")) else message.text.split(" ")
            command.parseParametersAndExecute(sender, message.from, message.chat, listOf(parsedValues, paramValue, unparsedValues).flatten().toMutableList())
        }
    }

    private fun sendHelpMessage(chatId: Long, wrongInput: String?): Message {
        return ChallengeBot.sendMessage(chatId, "${if (wrongInput != null) "Die Eingabe *$wrongInput* ist ungültig.\n" else ""}$helpText")
    }
}

class AllowedValuesParameter(name: String, helpText: String, val values: List<String>) : Parameter(name, helpText) {
    override fun isValueAllowed(value: String) = values.contains(value)

    override fun request(chat: Chat, command: Command, parsedValues: List<String>, unparsedValues: List<String>, wrongInput: String?) {
        val base = command.command + parsedValues.joinToString(prefix = " ", separator = " ", postfix = " ")
        val unparsed = if (unparsedValues.isEmpty()) "" else unparsedValues.joinToString(prefix = " ", separator = " ")
        ChallengeBot.sendMessage(chat.id,
                "${if (wrongInput != null) "Die Eingabe *$wrongInput* ist ungültig.\n" else ""}$helpText\nWähle einen der folgenden Werte aus:",
                inlineKeyboardFromPair(values.map { Pair(it, base + it + unparsed) })).also { MessageHandler.addDeleteableMessage(it, MessageType.REQUEST_MESSAGE) }

    }
}