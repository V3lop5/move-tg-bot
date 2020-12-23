package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.exit
import de.fhaachen.matse.movebot.telegram.model.Command
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object ShutdownCommand : Command("shutdown", "Irgendwann ist auch mal Schlafenszeit für den Bot. Speichert alle Daten und beendet den Bot.") {
    init {
        permissions.add(adminPermission)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>) {
        sendComplete(chat, "Der Bot wird nun beendet.")
        exit()
    }
}