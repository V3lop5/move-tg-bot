package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.botName
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.model.MovementType
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
        if (user.languageCode != null && user.languageCode != "de" && user.languageCode != "en") {
            sendMessage(chat, "This bot is only available in germany. Sorry! Your languageCode is ${user.languageCode}")
            return
        }

        ChallengerManager.addChallenger(user)
        sendComplete(
            chat,
            "*Pulse on Fire - Die Challenge*\n\nDiese Challenge wird dich 2021 auf Trab halten. Du hast ein Ziel: *2021 Punkte*\n\n" +
                    "Erfasse deine sportlichen Aktivitäten bei diesem Bot und beweise dir selbst, dass du sportlich bist! Du kannst zahlreiche Sportarten eintragen und um eine gute Platzierung in der Wochenstatistik kämpfen.\n\n" +
                    "Nimm dir 5 Minuten Zeit und schau dir das Tutorial an. Danach weißt du, wie du z.B. persönliche Ziele festlegst oder die anderen Teilnehmer der Challenge kennen lernen kannst.\nViel Erfolg!\n\n" +
                    MovementType.values().joinToString(separator = " ") { it.emoji },
            inlineKeyboardFromPair(
                Pair("Tutorial ansehen", TutorialCommand.command),
                Pair("Jahresziel erfassen", GoalCommand.command),
                Pair("Bewegung erfassen", AddMovementCommand.command), maxColumns = 1
            )
        )
    }
}