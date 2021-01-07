package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object SuspiciousCommand : ChallengerCommand("suspicious", "Suspicious Status eines Users ändern") {

    init {
        onlyUserChat()
        permissions.add(adminPermission)
        parameters += Parameter("Teilnehmer", "Für welchen Teilnehmer möchtest du den Suspicious Status ändern?")
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val suspect = ChallengerManager.findChallenger(params.first())

        if (suspect == null) {
            sendComplete(chat, "Kein Teilnehmer für _${params.first()}_ gefunden.")
            return
        }

        if (suspect.suspicious) {
            suspect.suspicious = false
            ChallengeBot.sendMessage(suspect.telegramUser.id, "Du wirst nun nicht mehr verdächtigt. Du kannst den Bot wieder normal benutzen.")
            sendComplete(chat, "Der Teilnehmer ${suspect.nickname.escapeMarkdown()} wird nun *nicht mehr verdächtigt*!")
        }else {
            suspect.suspicious = true
            ChallengeBot.sendMessage(suspect.telegramUser.id, "Du wirst verdächtigt falsche Angaben zu tätigen. Dies nimmt den anderen Teilnehmern der Challenge den Spaß. Deshalb wurdest du bis auf Weiteres von der Benutzung dieses Bots ausgeschlossen.\n\n" +
                    "Du glaubst es handelt sich um einen Fehler? Dann trete mit uns in Kontakt, damit wir den Irrtum klären können.", inlineKeyboardFromPair("Feedback geben" to FeedbackCommand.command))
            sendComplete(chat, "Der Teilnehmer ${suspect.nickname.escapeMarkdown()} ist nun *verdächtigt*!\nEr darf keine weiteren Aktivitäten eintragen und wurde vorerst aus den Statistiken entfernt.")
        }
    }
}