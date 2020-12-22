package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.botName
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object StartCommand : Command("start", "Für die erstmalige Benutzung des *${botName}s*!") {

    init {
        onlyUserChat()
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>) {
        if (user.languageCode != null && user.languageCode != "de") {
            sendMessage(chat, "This bot is only available in germany. Sorry!")
            return
        }

        ChallengerManager.addChallenger(user)
        sendComplete(chat, "*MOVE! - Deine Challenge*\n\nErreiche dieses Jahr 2021 Punkte!\n\nDer *$botName* hilft dir, dein Ziel im Auge zu behalten. Er speichert für dich die zurückgelegten Kilometer oder die Aktivitätsdauer.",
                inlineKeyboardFromPair(
                        Pair("Bewegung erfassen", AddMovementCommand.command),
                        Pair("Tutorial", TutorialCommand.command)))
    }
}