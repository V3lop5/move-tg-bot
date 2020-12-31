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


object MsgCommand : ChallengerCommand("msg", "Kurznachricht an Teilnehmer verschicken.") {
    init {
        onlyUserChat()
        permissions.add(adminPermission)
        parameters += Parameter("Teilnehmer", "Wem möchtest du eine Nachricht schicken?")
        parameters += Parameter("Nachricht", "Welche Nachricht soll an die ausgewählte Person verschickt werden?", singleWord = false)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val receiver = ChallengerManager.findChallenger(params.first())

        if (receiver == null) {
            sendComplete(chat, "Kein Teilnehmer für _${params.first()}_ gefunden.")
            return
        }

        val message = "_${challenger.nickname.escapeMarkdown()} hat dir eine Nachricht geschickt:_\n" + params[1].replace("\\n", "\n")

        ChallengeBot.sendMessage(receiver.telegramUser.id, message)
        sendComplete(chat, "Die Nachricht wurde an ${receiver.nickname} verschickt.\n\nSo sieht die Nachricht aus:\n$message")
    }

}
