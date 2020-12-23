package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.methods.send.SendVideo
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender

object WhoisCommand : ChallengerCommand("whois", "Videos und Ziele anderer Teilnehmer anschauen.") {

    val tutorial = mutableListOf<String>()

    init {
        onlyUserChat()
        requirements += allowPersonalShareRequirement

        parameters += Parameter("Teilnehmer", "Von welchem Teilnehmer möchtest du die Ziele und das Präsentationsvideo einsehen?\nGebe den Namen ein", singleWord = false)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val user = ChallengerManager.findChallenger(params.first())

        if (user == null){
            sendMessage(chat, "Dieser Teilnehmer wurde leider nicht gefunden. Unter /${CompetitorCommand.command} siehst du die Namen aller Teilnehmer.")
            return
        }

        if (!user.shareVideoAndGoals) {
            sendComplete(chat, "Dieser Teilnehmer möchte nicht, dass andere seine Ziele einsehen können.")
            return
        }

        val message = "*${user.nickname}* hat ${user.goals.size} Ziele:\n" +
                user.goals.map { (type, goal) ->
            StatisticsManager.getSum(user, type).let { "${type.name} $it ${type.unit} / *$goal ${type.unit}* (${(minOf(it/goal,1.0) * 100).toInt()} %)" }
        }.joinToString(prefix = "- ", separator = "\n- ")

        try {
            ChallengeBot.execute(SendVideo().setVideo(user.presentationVideoId).setChatId(chat.id).setCaption(message).setParseMode("markdown"))
        } catch (e: Exception) {
            sendComplete(chat, "Leider konnte ich die Informationen nicht zustellen. Falls das Problem öfters auftritt, melde dich unter /${FeedbackCommand.command}")
        }
    }
}