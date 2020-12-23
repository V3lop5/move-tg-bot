package de.fhaachen.matse.movebot.telegram.model

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

class Requirement(
        val message: String,
        val check: (user: User, chat: Chat, args: Array<out String>) -> Boolean)