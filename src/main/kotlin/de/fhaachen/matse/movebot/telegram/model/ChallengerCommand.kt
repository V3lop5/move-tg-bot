package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.model.Challenger
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

abstract class ChallengerCommand(command: String,
                                 desc: String) : Command(command, desc) {
    init {
        onlyUserChat()
        permissions.add(challengerPermission)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, params: List<String>) {
        handle(sender, user, chat, ChallengerManager.findChallenger(user)!!, params)
    }

    abstract fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>)
}