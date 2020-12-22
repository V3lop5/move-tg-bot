package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.bots.AbsSender

object RequestHandler {

    private val requests = mutableMapOf<Long, (sender: AbsSender, message: Message) -> Unit>()

    fun hasPendingRequest(chat: Chat) = requests.containsKey(chat.id)

    fun processUpdate(message: Message) {
        if (requests.containsKey(message.chatId)) {
            MessageHandler.addDeleteableMessage(message, MessageType.REQUESTED_VALUE)
            removeRequest(message.chatId)?.invoke(ChallengeBot, message)
        }
    }

    fun requestMessage(chat: Chat, request: Message, onInput: (sender: AbsSender, message: Message) -> Unit) {
        MessageHandler.addDeleteableMessage(request, MessageType.REQUEST_MESSAGE)
        requests[chat.id] = onInput
    }

    fun removeRequest(chatId: Long): ((AbsSender, Message) -> Unit)? {
        MessageHandler.cleanupMessages(chatId, MessageCleanupCause.COMMAND_PARAMETER_REQUEST_FINISHED)
        return requests.remove(chatId)
    }
}

