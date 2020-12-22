package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object NicknameCommand : ChallengerCommand("nickname", "Ändere deinen Anzeigenamen.") {
    init {
        parameters += object : Parameter("Name", "Gebe einen Namen ein unter dem du den anderen Challengern angezeigt werden möchtest.\nDer Name darf nur aus Buchstaben und Zahlen bestehen.", singleWord = false) {
            override fun isValueAllowed(value: String) = value.matches(Regex("^[a-zA-Z0-9 ]*$"))
        }

    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val nickname = params[0]
        challenger.customNickname = nickname
        sendComplete(chat, "Dein Anzeigename wurde auf *${challenger.nickname}* geändert.")
    }
}