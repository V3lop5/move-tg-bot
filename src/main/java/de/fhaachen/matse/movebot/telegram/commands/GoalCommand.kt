package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object GoalCommand :
    ChallengerCommand("goal", "Lege dir ein persönliches Ziel fest. Überlege es dir gut, der Bot wird dich erinnern!") {

    init {
        requirements += notSuspiciousRequirement

        onlyUserChat()
        parameters.add(movementTypeParameter)
        parameters.add(goalParameter)
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.of(params[0])
        val goal = params[1].toInt()

        if (!challenger.canChangeGoal(movementType)) {
            sendComplete(
                chat,
                "Sorry, du kannst dein Ziel für die Sportart ${movementType.title} nicht mehr anpassen. Du hast dir bereits *${challenger.goals[movementType]} ${movementType.unit}* vorgenommen. Zähne zusammenbeißen und durch!"
            )
        } else {
            challenger.setGoal(movementType, goal)
            sendComplete(
                chat,
                "Dein Ziel für die Sportart *${movementType.title}* wurde auf *$goal ${movementType.unit}* gesetzt. Viel Erfolg!"
            )
        }

        val possibleGoals =
            MovementType.values().filterNot { challenger.goals.containsKey(it) }.filter { challenger.canChangeGoal(it) }
                .map { it.name to command + " " + it.name }
        if (possibleGoals.isNotEmpty())
            sendMessage(chat, "Möchtest du ein weiteres Jahresziel festlegen?", inlineKeyboardFromPair(possibleGoals))
    }
}