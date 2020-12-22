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


object BroadcastCommand : ChallengerCommand("broadcast", "Informiert alle Teilnehmer über neue Features.") {
    init {
        onlyUserChat()
        permissions.add(adminPermission)
        parameters.add(Parameter("Nachricht", "Welche Nachricht soll an alle Challenger verschickt werden?", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val message = params[0].replace("\\n", "\n") + "\n_Diese Nachricht wurde von ${challenger.nickname.escapeMarkdown()} verschickt._"

        var count = 0
        ChallengerManager.challengers.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message)
                count++
            } catch (e: Exception) {
            }
        }

        sendComplete(chat, "Die Nachricht wurde an $count Challenger verschickt.")
    }

}
