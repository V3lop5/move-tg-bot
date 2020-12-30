package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair

object KeyboardRequestHandler {

    private val openRequests = mutableListOf<KeyboardRequest>()

    fun sendRequest(
        chatIds: List<Long>,
        message: String,
        answers: List<String>,
        action: (user: Long, answer: String) -> Unit
    ) {
        val id = generateId()
        val keyboard = inlineKeyboardFromPair(answers.mapIndexed{ index, answer -> answer to "#kbreq $id $index"})
        val messages = chatIds.map { ChallengeBot.sendMessage(id, message, keyboard) }.map { it.chatId to it.messageId }
        openRequests += KeyboardRequest(id, messages, answers, action)
    }

    private fun generateId(): Int {
        return openRequests.lastOrNull()?.id?:1
    }

    fun onAnswer(chatId: Long, id: Int, answerId: Int) {
        val request = openRequests.find { it.id == id }

        if ( request == null) {
            ChallengeBot.sendMessage(chatId, "Sorry, dieser Knopfdruck konnte nicht verarbeitet werden.")
            return
        }

        request.action(chatId, request.answers[answerId])
        request.messages.forEach { (chatId, messageId) -> MessageHandler.deleteMessage(chatId, messageId) }
        openRequests -= request
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
        val action: (chatId: Long, answer: String) -> Unit
    )
}