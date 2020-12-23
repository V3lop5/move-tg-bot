package de.fhaachen.matse.movebot.telegram.model

import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User

class CommandPermission(
        val message: String,
        val check: (user: User, chat: Chat) -> Boolean)