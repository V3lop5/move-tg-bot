package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.telegram.ConfirmHandler
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.RequestHandler
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object CancelCommand : Command("cancel", "Bricht die aktuelle Aktion ab.") {
    override fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>) {
        RequestHandler.removeRequest(chat.id)
        ConfirmHandler.removeConfirmation(chat.id)
        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.COMMAND_CANCELED)
        sendComplete(chat, "Die Aktion wurde abgebrochen.")
    }
}