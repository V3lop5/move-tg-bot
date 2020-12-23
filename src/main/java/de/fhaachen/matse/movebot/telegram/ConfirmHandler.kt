package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.telegram.commands.CancelCommand
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

object ConfirmHandler {

    private val confirmations = mutableMapOf<Long, (decision: Boolean) -> Unit>()
    private val confirmationState = mutableMapOf<Long, Boolean>()

    fun hasPendingConfirmation(chat: Chat) = confirmations.containsKey(chat.id)

    fun hasAnsweredConfirmation(id: Long) = confirmationState.containsKey(id)

    fun hasConfirmed(id: Long) = confirmationState[id] ?: false

    fun processUpdate(decision: Boolean, from: User) {
        val id = from.id.toLong()
        if (confirmations.containsKey(id)) {
            confirmationState[id] = decision
            confirmations[id]?.invoke(decision)
            removeConfirmation(id)
        }
    }

    fun requestConfirmation(chat: Chat, request: String, onInput: (decision: Boolean) -> Unit) {
        removeConfirmation(chat.id)
        ChallengeBot.sendMessage(chat.id, request, inlineKeyboardFromPair(Pair("Ja", "# ${true}"), Pair("Nein", "# ${false}"), Pair("Abbrechen", CancelCommand.command)))
                .also {
                    MessageHandler.addDeleteableMessage(it, MessageType.CONFIRM_REQUEST)
                }

        confirmations[chat.id] = onInput
    }

    fun removeConfirmation(chatId: Long) {
        MessageHandler.cleanupMessages(chatId, MessageCleanupCause.CONFIRM_COMPLETE)
        confirmationState.remove(chatId)
        confirmations.remove(chatId)
    }

    fun removeAllConfirmationRequests() {
        confirmations.keys.forEach(ConfirmHandler::removeConfirmation)
    }
}