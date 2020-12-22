package de.fhaachen.matse.movebot.telegram.model

val userchatRequirement = Requirement("Dieser Befehl kann nur im privaten Chat ausgeführt werden.") { user, chat, args -> chat.isUserChat }