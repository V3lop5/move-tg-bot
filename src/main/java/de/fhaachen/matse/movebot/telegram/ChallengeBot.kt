package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.botName
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.LiveLocationManager
import de.fhaachen.matse.movebot.handler.ChallengerHandler
import de.fhaachen.matse.movebot.model.ChallengerPermission
import de.fhaachen.matse.movebot.telegram.ChallengeBot.registerDefaultAction
import de.fhaachen.matse.movebot.telegram.commands.*
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import java.io.File

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
        register(GoalCommand)
        register(HelpCommand)
        register(JoinTeamCommand)
        register(LeaderboardCommand)
        register(MsgCommand)
        register(NewPlanCommand)
        register(NewReminderCommand)
        register(NewTeamCommand)
        register(NewTeamFightCommand)
        register(NicknameCommand)
        register(ShutdownCommand)
        register(StartCommand)
        register(TeamFightCommand)
        register(TutorialCommand)
        register(WhoisCommand)

        registerDefaultAction { _, message ->
            sendMessage(
                message.chatId,
                "Der Befehl *${message.text.split("\\s+")[0]}* existiert nicht.\nAlle Befehle: /${HelpCommand.command}"
            )
        }
    }

    private val lastVideoIds = mutableMapOf<User, String>()

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
            else if (update.message.hasVideo()) {
                lastVideoIds[update.message.from] = update.message.video.fileId
                MessageHandler.cleanupMessages(update.message.chatId, MessageCleanupCause.COMMAND_CANCELED)
                sendMessage(
                    update.message.chatId, "Möchtest du dieses Video als dein Vorstellungsvideo verwenden?",
                    inlineKeyboardFromPair("Ja" to "#myvideo", "Nein" to CancelCommand.command)
                )
                    .also { MessageHandler.addDeleteableMessage(it, MessageType.COMMAND_PROCESS) }
                downloadFile(execute(GetFile().setFileId(update.message.video.fileId))).copyTo(File("videos/${update.message.video.fileId}.mp4"))
            } else
                println("[processNonCommandUpdate Other] ${update.message.from.getName()} (${update.message.from.id}) message='${update.message}'")
        } else if (update.hasEditedMessage()) {
            if (update.editedMessage.hasLocation()) {
                LiveLocationManager.onLiveLocationUpdate(update.editedMessage)
            } else if (update.editedMessage.hasText()) {
                println("[processNonCommandUpdate EditedText] ${update.editedMessage.from.getName()} (${update.editedMessage.from.id}) text='${update.editedMessage.text}'")
                sendMessage(
                    update.editedMessage.chatId,
                    "Hast du gerade eine Nachricht editiert? Ich hab nicht aufgepasst.\nBitte gebe deine Nachricht erneut ein. Editierung unterstütze ich momentan nicht."
                )
            }
        } else if (update.hasCallbackQuery()) {
            var dataSplit = update.callbackQuery.data.split(Regex("\\s+"))

            if (dataSplit[0].startsWith("#endlive")) {
                val distance = LiveLocationManager.endLiveLocation(update.callbackQuery.message.chatId)
                dataSplit = listOf(AddMovementCommand.command, "<REQUEST>", distance.toString(), "heute")
            }

            if (dataSplit.first() == "#kbreq") {
                KeyboardRequestHandler.onAnswer(
                    update.callbackQuery.from.id,
                    dataSplit.component2().toInt(),
                    dataSplit.component3().toInt()
                )
                return
            }

            // TODO Nicht schön gelöst
            if (dataSplit[0].startsWith("#reminder")) {
                dataSplit = dataSplit.drop(1)
                MessageHandler.cleanupMessages(
                    update.callbackQuery.from.id.toLong(),
                    MessageCleanupCause.REMINDER_CLICKED
                )
                if (dataSplit.isEmpty()) return // Abbrechen wurde gedrückt
            }

            if (dataSplit[0].startsWith("#myvideo")) {
                MessageHandler.cleanupMessages(
                    update.callbackQuery.from.id.toLong(),
                    MessageCleanupCause.COMMAND_COMPLETE
                )
                val videoId = lastVideoIds[update.callbackQuery.from]
                if (videoId == null) {
                    sendMessage(update.callbackQuery.from.id.toLong(), "VideoId nicht gefunden.")
                    return
                }
                ChallengerManager.findChallenger(update.callbackQuery.from)?.run {
                    presentationVideoId = videoId
                    shareVideoAndGoals = false
                    isVideoAccepted = false
                    sendMessage(
                        update.callbackQuery.from.id.toLong(),
                        "Dein Vorstellungsvideo wurde aktualisiert. Dürfen das Video andere Challenge-Teilnehmer sehen?",
                        inlineKeyboardFromPair("Ja" to "#allowSharing", "Nein" to CancelCommand.command)
                    )
                }
                return
            }

            if (dataSplit[0].startsWith("#allowSharing")) {
                MessageHandler.cleanupMessages(
                    update.callbackQuery.from.id.toLong(),
                    MessageCleanupCause.COMMAND_COMPLETE
                )
                ChallengerManager.findChallenger(update.callbackQuery.from)?.run {
                    shareVideoAndGoals = true
                    sendMessage(
                        update.callbackQuery.from.id.toLong(),
                        "Freigabe-Einstellungen gespeichert. " +
                                "${if (shareVideoAndGoals) "Andere dürfen dein Video und deine Ziele einsehen." else "Niemand darf dein Video und deine Ziele einsehen."}\n\n" +
                                "Ich überprüfe dein Video. Sobald das Video freigeschaltet ist, kannst du die Videos der anderen Teilnehmer einsehen."
                    )
                    ChallengerHandler.onVideoAdd(this)

                    if (shareVideoAndGoals) {
                        KeyboardRequestHandler.addRequest(
                            ChallengerManager.challengers.filter { it.hasPermission(ChallengerPermission.MODERATOR) }
                                .map { it.telegramUser.id },
                            listOf("Annehmen", "Ablehnen"),
                            { chatId, keyboard ->
                                ChallengeBot.execute(
                                    SendVideo()
                                        .setVideo(presentationVideoId)
                                        .setChatId(chatId.toLong())
                                        .setCaption("Darf dieses Video von ${nickname} mit allen geteilt werden?")
                                        .setParseMode("markdown")
                                        .setReplyMarkup(keyboard)
                                )
                            },
                            { _, answer ->
                                if (answer == "Annehmen") {
                                    isVideoAccepted = true
                                    sendMessage(
                                        telegramUser.id,
                                        "Dein Video wurde akzeptiert. Du kannst dir jetzt die Vorstellungsvideos der anderen Teilnehmer ansehen.",
                                        inlineKeyboardFromPair("Vorstellungen anstehen" to WhoisCommand.command)
                                    )
                                    ChallengerManager.challengers.filter { it.telegramUser.id != telegramUser.id }.forEach {
                                        sendMessage(
                                            it.telegramUser.id,
                                            "$nickname hat ein Vorstellungsvideo hochgeladen.\n" +
                                                    if (!it.shareVideoAndGoals) "Damit du dir das Video ansehen kannst, musst du selbst ein Video aufnehmen und an diesen Bot schicken." else "",
                                            inlineKeyboardFromPair("Jetzt ansehen" to WhoisCommand.command + " " + nickname)
                                        )
                                    }
                                } else {
                                    sendMessage(
                                        telegramUser.id,
                                        "Dein Video wurde leider abgelehnt. Bitte lade ein neues Video hoch."
                                    )
                                }
                            }
                        ) }
                }
                return
            }

            if (dataSplit[0].startsWith("#"))
                return ConfirmHandler.processUpdate(dataSplit[1].toBoolean(), update.callbackQuery.from)

            val command = getRegisteredCommand(dataSplit[0])
            (command as? Command)?.execute(
                this,
                update.callbackQuery.from,
                update.callbackQuery.message.chat,
                dataSplit.drop(1).toTypedArray()
            )
                .apply {
                    execute(
                        AnswerCallbackQuery().setCallbackQueryId(update.callbackQuery.id)
                            .setText("Die Anfrage wurde verarbeitet.")
                    )
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
        KeyboardRequestHandler.cleanupAllRequests()
    }

    fun sendEditText(message: Message, text: String, keyboard: InlineKeyboardMarkup? = null) {
        val editMsg =
            EditMessageText().setChatId(message.chatId).setMessageId(message.messageId).setParseMode("markdown")
                .setText(text)
        if (keyboard != null) editMsg.replyMarkup = keyboard
        execute(editMsg)
    }

}