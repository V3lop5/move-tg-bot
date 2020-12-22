package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.botName
import de.fhaachen.matse.movebot.control.LiveLocationManager
import de.fhaachen.matse.movebot.telegram.ChallengeBot.registerDefaultAction
import de.fhaachen.matse.movebot.telegram.commands.*
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

object ChallengeBot : TelegramLongPollingCommandBot(botName) {

    init {
        register(AddMovementCommand)
        register(AddTrainingCommand)
        register(BotStatusCommand)
        register(BroadcastCommand)
        register(CancelCommand)
        register(CompetitorCommand)
        register(ChallengeCommand)
        register(DiagramCommand)
        register(FeedbackCommand)
        register(HelpCommand)
        register(NewPlanCommand)
        register(NewReminderCommand)
        register(NicknameCommand)
        register(StartCommand)
        register(ShutdownCommand)
        register(TutorialCommand)

        registerDefaultAction { _, message ->
            sendMessage(message.chatId, "Der Befehl *${message.text.split("\\s+")[0]}* existiert nicht.\nAlle Befehle: /${HelpCommand.command}")
        }

    }


    override fun processNonCommandUpdate(update: Update) {
        if (update.hasMessage()) {
            if (update.message.hasText()) {
                println("[processNonCommandUpdate Text] ${update.message.from.getName()} (${update.message.from.id}) text='${update.message.text}'")
                if (RequestHandler.hasPendingRequest(update.message.chat)) {
                    RequestHandler.processUpdate(update.message)
                    return
                }
            } else if (update.message.hasLocation())
                LiveLocationManager.onLocationMessage(update.message)
            else
                println("[processNonCommandUpdate Other] ${update.message.from.getName()} (${update.message.from.id}) message='${update.message}'")
        } else if (update.hasEditedMessage()) {
            if (update.editedMessage.hasLocation()) {
                LiveLocationManager.onLiveLocationUpdate(update.editedMessage)
            }
        } else if (update.hasCallbackQuery()) {
            var dataSplit = update.callbackQuery.data.split(Regex("\\s+"))

            if (dataSplit[0].startsWith("#endlive")) {
                val distance = LiveLocationManager.endLiveLocation(update.callbackQuery.message.chatId)
                dataSplit = listOf(AddMovementCommand.command, "<REQUEST>", distance.toString(), "heute")
            }

            // TODO Nicht schön gelöst
            if (dataSplit[0].startsWith("#reminder")) {
                dataSplit = dataSplit.drop(1)
                MessageHandler.cleanupMessages(update.callbackQuery.from.id.toLong(), MessageCleanupCause.REMINDER_CLICKED)
                if (dataSplit.isEmpty()) return // Abbrechen wurde gedrückt
            }

            if (dataSplit[0].startsWith("#"))
                return ConfirmHandler.processUpdate(dataSplit[1].toBoolean(), update.callbackQuery.from)

            val command = getRegisteredCommand(dataSplit[0])
            (command as? Command)?.execute(this, update.callbackQuery.from, update.callbackQuery.message.chat, dataSplit.drop(1).toTypedArray())
                    .apply {
                        execute(AnswerCallbackQuery().setCallbackQueryId(update.callbackQuery.id).setText("Die Anfrage wurde verarbeitet."))
                    }
                    ?: println("Command '${dataSplit[0]}' für CallbackQuery '${update.callbackQuery.data}' nicht gefunden.")
        }
    }

    override fun getBotToken() = de.fhaachen.matse.movebot.botToken

    fun sendMessage(chatId: Long, text: String, keyboard: ReplyKeyboard? = null): Message {
        val msg = SendMessage(chatId, text).setParseMode("markdown")
        if (keyboard != null) msg.replyMarkup = keyboard

        return execute(msg)
    }

    fun sendMessage(userId: Int, text: String, keyboard: ReplyKeyboard? = null) =
            sendMessage(userId.toLong(), text, keyboard)

    fun shutdownBot() {
        MessageHandler.cleanupAllMessages()
        ConfirmHandler.removeAllConfirmationRequests()
    }

    fun sendEditText(message: Message, text: String, keyboard: InlineKeyboardMarkup? = null) {
        val editMsg = EditMessageText().setChatId(message.chatId).setMessageId(message.messageId).setParseMode("markdown").setText(text)
        if (keyboard != null) editMsg.replyMarkup = keyboard
        execute(editMsg)
    }

}