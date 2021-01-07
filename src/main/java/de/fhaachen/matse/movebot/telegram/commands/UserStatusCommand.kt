package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.padStart
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.adminPermission
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object UserStatusCommand : ChallengerCommand("userstatus", "Status eines Users anzeigen") {

    init {
        onlyUserChat()
        permissions.add(adminPermission)
        parameters += Parameter("Teilnehmer", "Welchen Teilnehmer möchtest du sehen?")
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {

        val suspect = ChallengerManager.findChallenger(params.first())

        if (suspect == null) {
            sendComplete(chat, "Kein Teilnehmer für _${params.first()}_ gefunden.")
            return
        }

        val message = "Infos über *${suspect.nickname.escapeMarkdown()}*\n\n" +
                "*Telegram:*\n" +
                " > ID: ${suspect.telegramUser.id} ${if (suspect.telegramUser.bot) " (Bot)" else ""}\n" +
                " > Sprache: ${suspect.telegramUser.languageCode}\n" +
                " > Vorname: ${suspect.telegramUser.firstName.escapeMarkdown().ifBlank { "_nicht gesetzt_" }}\n" +
                " > Nachname: ${suspect.telegramUser.lastName.escapeMarkdown().ifBlank { "_nicht gesetzt_" }}\n" +
                " > Benutzername: ${suspect.telegramUser.userName.escapeMarkdown().ifBlank { "_nicht gesetzt_" }}\n\n" +
                "*Teilnehmer:*\n" +
                " > Anzeigename: ${suspect.nickname.escapeMarkdown()}\n" +
                " > Beitritt: ${suspect.joinTimestamp.prettyString()}\n" +
                " > Pläne: ${suspect.plans.size}\n" +
                " > Erinnerungen: ${suspect.reminders.size}\n" +
                " > Video geteilt: ${suspect.isVideoAccepted}\n\n" +
                "Jahresziele:\n${suspect.goals.map { (type, goal) -> 
                    StatisticsManager.getSum(suspect, type).let { "`${(minOf(it/goal,1.0) * 100).toInt().padStart(3)}` % ${type.emoji}   $it von *$goal ${type.unit}* ${type.title} " } 
                }.joinToString(prefix = "- ", separator = "\n- ")}\n\n" +
                "Nach Bewegungstyp:\n${MovementType.values().map { Triple(it, StatisticsManager.getSum(suspect, it), StatisticsManager.getPoints(suspect, it)) }.filter { it.second > 0 }
                    .map { (type, sum, points) -> "${type.emoji} `${sum.padStart(4)}` ${type.unit} *${type.name}* ($points Pkt.)" }.joinToString(prefix = "", separator = "\n")}\n\n" +
                "*Letzte Aktivitäten:*\n${
                suspect.movements.sortedBy { it.datetime }.takeLast(5).joinToString(prefix = "- ", separator = "\n- ") { "${it.value} ${it.type.unit} ${it.type.name} (${getRelativeTimeSpan(it.datetime)})" }}\n\n" +
                "${suspect.nickname.escapeMarkdown()} erreichte in ${suspect.movements.size} Aktivitäten *${StatisticsManager.getPoints(suspect)}* Punkte!"
        sendComplete(chat, message, inlineKeyboardFromPair("Nachricht senden" to "${MsgCommand.command} ${suspect.telegramUser.id}"))
    }
}