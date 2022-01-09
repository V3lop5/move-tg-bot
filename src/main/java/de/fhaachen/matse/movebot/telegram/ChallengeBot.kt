package de.fhaachen.matse.movebot.telegram

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.LiveLocationManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.handler.ChallengerHandler
import de.fhaachen.matse.movebot.model.ChallengerPermission
import de.fhaachen.matse.movebot.telegram.commands.*
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.bots.DefaultBotOptions
import org.telegram.telegrambots.extensions.bots.commandbot.TelegramLongPollingCommandBot
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery
import org.telegram.telegrambots.meta.api.methods.GetFile
import org.telegram.telegrambots.meta.api.methods.send.SendDocument
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import java.io.File

object ChallengeBot : TelegramLongPollingCommandBot(DefaultBotOptions()) {

    init {
        register(AddMovementCommand)
        register(AddTrainingCommand)
        register(BotStatusCommand)
        register(BroadcastCommand)
        register(BroadcastVideoCommand)
        register(CancelCommand)
        register(CompetitorCommand)
        register(ChallengeCommand)
        register(DiagramCommand)
        register(ExportCSVCommand)
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
        register(SuspiciousCommand)
        register(StartCommand)
        register(TeamFightCommand)
        register(TeamCommand)
        register(TutorialCommand)
        register(UserStatusCommand)
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
                if (RequestHandler.hasPendingRequest(update.message.chat)) {
                    RequestHandler.processUpdate(update.message)
                    return
                } else {
                    println("[processNonCommandUpdate Text] ${update.message.from.getName()} (${update.message.from.id}) text='${update.message.text}'")
                    sendMessage(
                        update.message.chatId,
                        "Wo waren wir? Sorry, ich hab nicht aufgepasst.\n" +
                                "Klicke auf einen Button oder gebe ein Befehl (z.B. /${AddMovementCommand.command}) ein.",
                        inlineKeyboardFromPair(
                            "Tutorial ansehen" to TutorialCommand.command,
                            "Feedback geben" to FeedbackCommand.command,
                            "Aktivität erfassen" to AddMovementCommand.command
                        )
                    )
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

                val fileGetter = GetFile()
                fileGetter.fileId = update.message.video.fileId

                val downloadedFile = downloadFile(execute(fileGetter))
                downloadedFile.copyTo(File("videos/${update.message.video.fileId}.mp4"))
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
                                val request = SendVideo()
                                request.video = InputFile(presentationVideoId)
                                request.chatId = chatId.toString()
                                request.caption =
                                    "Darf dieses Video von ${nickname.escapeMarkdown()} mit allen geteilt werden?"
                                request.parseMode = "markdown"
                                request.replyMarkup = keyboard
                                return@addRequest ChallengeBot.execute(request)
                            }
                        ) { _, answer ->
                            if (answer == "Annehmen") {
                                isVideoAccepted = true
                                sendMessage(
                                    telegramUser.id,
                                    "Dein Video wurde akzeptiert. Du kannst dir jetzt die Vorstellungsvideos der anderen Teilnehmer ansehen.",
                                    inlineKeyboardFromPair("Vorstellungen anstehen" to WhoisCommand.command)
                                )
                                ChallengerManager.challengers.filter { it.telegramUser.id != telegramUser.id }
                                    .forEach {
                                        sendMessage(
                                            it.telegramUser.id,
                                            "${nickname.escapeMarkdown()} hat ein Vorstellungsvideo hochgeladen.\n" +
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
                    }
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
                    val request = AnswerCallbackQuery()
                    request.callbackQueryId = update.callbackQuery.id
                    request.text = "Die Anfrage wurde verarbeitet."
                    execute(request)
                }
                ?: println("Command '${dataSplit[0]}' für CallbackQuery '${update.callbackQuery.data}' nicht gefunden.")
        }
    }

    override fun getBotToken() = de.fhaachen.matse.movebot.botToken
    override fun getBotUsername() = de.fhaachen.matse.movebot.botName

    fun sendMessage(chatId: Long, text: String, keyboard: ReplyKeyboard? = null): Message {
        val msg = SendMessage(chatId.toString(), text)
        msg.parseMode = "markdown"
        if (keyboard != null) msg.replyMarkup = keyboard

        return execute(msg)
    }


    fun sendPhoto(chatId: Long, photo: File, caption: String): Message? {
        val msg = SendPhoto()
        msg.chatId = chatId.toString()
        msg.photo = InputFile(photo)
        msg.caption = caption
        msg.parseMode = "markdown"
        return execute(msg)
    }

    fun sendDocument(chatId: Long, file: File, caption: String): Message? {
        val msg = SendDocument()
        msg.chatId = chatId.toString()
        msg.document = InputFile(file)
        msg.caption = caption
        msg.parseMode = "markdown"
        return execute(msg)
    }

    fun shutdownBot() {
        MessageHandler.cleanupAllMessages()
        ConfirmHandler.removeAllConfirmationRequests()
        KeyboardRequestHandler.cleanupAllRequests()
    }

    fun sendEditText(message: Message, text: String, keyboard: InlineKeyboardMarkup? = null) {
        val editMsg = EditMessageText()
        editMsg.chatId = message.chatId.toString()
        editMsg.messageId = message.messageId
        editMsg.parseMode = "markdown"
        editMsg.text = text
        if (keyboard != null) editMsg.replyMarkup = keyboard
        execute(editMsg)
    }

}