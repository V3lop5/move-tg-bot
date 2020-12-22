package de.fhaachen.matse.movebot.telegram

import org.telegram.telegrambots.meta.api.objects.User

fun User.getName() = if (firstName != null || lastName != null) {
    "${firstName ?: ""} ${lastName ?: ""}".trim()
} else if (userName != null) {
    userName
} else {
    null
}
