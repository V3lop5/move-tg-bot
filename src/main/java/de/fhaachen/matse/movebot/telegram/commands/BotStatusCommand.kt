package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.lastDataChange
import de.fhaachen.matse.movebot.lastDataSave
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender


object BotStatusCommand : ChallengerCommand("botstatus", "Zeigt den aktuellen Status des Bots an.") {
    init {
        onlyUserChat()
        permissions.add(adminPermission)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val status = "Informationen über den ${ChallengeBot.botUsername}" +
                "\n*Anzahl Teilnehmer*: ${ChallengerManager.countChallengers()} (${ChallengerManager.countChallengers(true)} aktiv)" +
                "\n*Erfasste Bewegungen*: ${ChallengerManager.challengers.sumBy { it.movements.size }}" +
                "\n*Letzte Datenänderung*: ${getRelativeTimeSpan(lastDataChange)}" +
                "\n*Letzte Speicherung*: ${getRelativeTimeSpan(lastDataSave)}"

        sendComplete(chat, status)
    }

}
