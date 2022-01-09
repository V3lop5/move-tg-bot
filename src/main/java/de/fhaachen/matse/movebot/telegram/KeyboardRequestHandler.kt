package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup

object KeyboardRequestHandler {

    private val openRequests = mutableListOf<KeyboardRequest>()

    fun addRequest(
        chatIds: List<Long>,
        answers: List<String>,
        sendMessage: (chatId: Long, keyboard: InlineKeyboardMarkup) -> Message,
        action: (user: Long, answer: String) -> Unit
    ) {
        val id = generateId()
        val keyboard = inlineKeyboardFromPair(answers.mapIndexed { index, answer -> answer to "#kbreq $id $index" })
        val messages = chatIds.mapNotNull {
            try {
                sendMessage(it, keyboard)
            } catch (e: Exception) {
                null
            }
        }.map { it.chatId to it.messageId }
        openRequests += KeyboardRequest(id, messages, answers, action)
    }

    private fun generateId(): Int {
        return openRequests.lastOrNull()?.id ?: 1
    }

    fun onAnswer(chatId: Long, id: Int, answerId: Int) {
        val request = openRequests.find { it.id == id }

        if (request == null) {
            ChallengeBot.sendMessage(chatId, "Sorry, dieser Knopfdruck konnte nicht verarbeitet werden.")
            return
        }
        try {
            request.action(chatId, request.answers[answerId])
            request.messages.forEach { (chatId, messageId) -> MessageHandler.deleteMessage(chatId, messageId) }
        } finally {
            openRequests -= request
        }
    }

    fun cleanupAllRequests() {
        openRequests.forEach {
            it.messages.forEach { (chatId, messageId) -> MessageHandler.deleteMessage(chatId, messageId) }
        }
    }

    private data class KeyboardRequest(
        val id: Int,
        val messages: List<Pair<Long, Int>>,
        val answers: List<String>,
        val action: (userId: Long, answer: String) -> Unit
    )
}