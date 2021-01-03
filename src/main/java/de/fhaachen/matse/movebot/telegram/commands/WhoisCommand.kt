package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object WhoisCommand : ChallengerCommand("whois", "Videos und Ziele anderer Teilnehmer anschauen.") {

    init {
        onlyUserChat()
        requirements += allowPersonalShareRequirement

        parameters += Parameter("Teilnehmer", "Von welchem Teilnehmer möchtest du die Ziele und das Präsentationsvideo einsehen?\nGebe den Namen des Teilnehmers ein.", singleWord = false)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val who = ChallengerManager.findChallenger(params.first())

        if (who == null){
            sendMessage(chat, "Dieser Teilnehmer wurde leider nicht gefunden. Unter /${CompetitorCommand.command} siehst du die Namen aller Teilnehmer. Probiere es erneut.")
            return
        }

        if (!who.shareVideoAndGoals) {
            sendComplete(chat, "Dieser Teilnehmer möchte seine persönlichen Ziele nicht teilen.")
            return
        }

        val message = "*${who.nickname.escapeMarkdown()}* hat ${who.goals.size} Ziele:\n" +
                who.goals.map { (type, goal) ->
            StatisticsManager.getSum(who, type).let { "${type.name} $it ${type.unit} / *$goal ${type.unit}* (${(minOf(it/goal,1.0) * 100).toInt()} %)" }
        }.joinToString(prefix = "- ", separator = "\n- ")

        try {
            ChallengeBot.execute(SendVideo().setVideo(who.presentationVideoId).setChatId(chat.id).setCaption(message).setParseMode("markdown"))
        } catch (e: Exception) {
            sendComplete(chat, "Leider konnte ich die Informationen nicht zustellen. Falls das Problem öfters auftritt, melde dich unter /${FeedbackCommand.command}")
        }
    }
}