package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.telegram.model.DeleteableMessage
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage
import org.telegram.telegrambots.meta.api.objects.Message

object MessageHandler {

    private val deleteableMessages = mutableListOf<DeleteableMessage>()

    fun addDeleteableMessage(message: Message, type: MessageType) {
        deleteableMessages.add(DeleteableMessage(message.messageId, message.chat.id, type))
    }

    fun cleanupMessages(chatId: Long, cleanupCause: MessageCleanupCause) {
        deleteableMessages.filter { it.chatId == chatId && it.isCleanedBy(cleanupCause) }.forEach {
            deleteMessage(it.chatId, it.messageId)
            deleteableMessages.remove(it)
        }
    }

    fun cleanupAllMessages() {
        deleteableMessages.groupBy { it.chatId }.keys.forEach { cleanupMessages(it, MessageCleanupCause.SHUTDOWN) }
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        try {
            ChallengeBot.execute(DeleteMessage(chatId, messageId))
        } catch (e: Exception) {
            System.err.println("Löschen der Nachricht ${messageId} in ${chatId} nicht möglich... Überspringe...")
        }
    }

    fun isDeleteableMessage(message: Message) = deleteableMessages.any { it.chatId == message.chatId && it.messageId == message.messageId }
}