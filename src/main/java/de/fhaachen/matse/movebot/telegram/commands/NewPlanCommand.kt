package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.model.Plan
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object NewPlanCommand : ChallengerCommand("newplan", "Du machst regelmäßig die gleiche Sporteinheit? Dann trage diese doch als Plan ein.") {
    init {
        requirements += notSuspiciousRequirement
        parameters.add(Parameter("Schlüsselwort", "Gebe ein Wort zur Wiedererkennung dieses Plans ein."))
        parameters.add(movementTypeParameter)
        parameters.add(movementValueParameter)
        parameters.add(Parameter("Beschreibung", "Gebe eine kurze Beschreibung deines Trainings an.", singleWord = false))

    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val keyword = params[0]
        val movementType = MovementType.valueOf(params[1])
        val distance = params[2].toDouble().round(2)
        val description = params[3]

        if (challenger.plans.any { it.keyword.equals(keyword, true) }) {
            sendComplete(chat, "Es existiert bereits ein Plan mit dem Schlüsselwort $keyword.")
            return
        }

        challenger.addPlan(Plan(keyword, movementType, distance, description))
        sendComplete(chat, "Der Plan wurde hinzugefügt! Mithilfe des Befehls /${AddTrainingCommand.command} kannst du eine Aktivität aus dem Plan erzeugen. Erzeugen?! Ach keine Ahnung wie man das am Besten beschreibt...")
    }
}