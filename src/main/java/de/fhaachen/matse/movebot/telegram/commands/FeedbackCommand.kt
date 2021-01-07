package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.ChallengerPermission
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender


object FeedbackCommand : ChallengerCommand("feedback", "Schreibe mir deine Anregungen/Wünsche.") {
    init {
        onlyUserChat()
        parameters.add(Parameter("Nachricht", "Bitte gebe dein Feedback ein.", singleWord = false))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val message = "*Feedback*\n" + params.first()
            .replace("\\n", "\n") + "\n_Diese Nachricht wurde von ${challenger.nickname.escapeMarkdown()} verschickt._"

        ChallengerManager.challengers.filter { it.hasPermission(ChallengerPermission.ADMIN) }.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message, inlineKeyboardFromPair("Teilnehmer ansehen" to "${UserStatusCommand.command} ${challenger.telegramUser.id}", "Antworten" to "${MsgCommand.command} ${challenger.telegramUser.id}"))
            } catch (e: Exception) {
            }

        }

        sendComplete(chat, "Vielen Dank für dein Feedback!")


    }

}
