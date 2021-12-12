package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.io.File


object BroadcastVideoCommand : ChallengerCommand("broadcastvideo", "Verschickt Video an alle Teilnehmer.") {
    init {
        onlyUserChat()
        permissions.add(adminPermission)
        parameters.add(Parameter("Videofile", "Welches Video soll verschickt werden?"))
        parameters.add(Parameter("Videounterschrift", "Welche Nachricht soll an alle Challenger verschickt werden?", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val file = File("videos/${params[0]}")

        if(!file.exists()) {
            sendComplete(chat, "Das Video wurde nicht gefunden!")
            return
        }

        val message = params[1].replace("\\n", "\n") + "\n_Dieses Video wurde von ${challenger.nickname.escapeMarkdown()} verschickt._"

        var count = 0
        ChallengerManager.challengers.filter { !it.suspicious }.forEach {
            try {
                ChallengeBot.sendDocument(it.telegramUser.id.toLong(), file, message)
                count++
            } catch (e: Exception) {
            }
        }

        sendComplete(chat, "Die Nachricht wurde an $count Challenger verschickt.")
    }

}
