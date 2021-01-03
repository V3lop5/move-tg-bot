package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.POINT_GOAL
import de.fhaachen.matse.movebot.escapeMarkdown
import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.events.MovementAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import java.time.Duration

object GoalComplete : MovementAdd {


    fun register() {
        MovementHandler.movementAddListener += this
    }

    override fun onMovementAdd(challenger: Challenger, movement: Movement) {

        val goal = challenger.goals[movement.type]?: return

        val newValue = StatisticsManager.getSum(challenger, movement.type)
        val oldValue = newValue - movement.value


        if (newValue < goal || oldValue >= goal)
            return

        complete(challenger, movement.type, goal, challenger.shareVideoAndGoals)
    }

    fun complete(challenger: Challenger, type: MovementType, goal: Int, shareVideoAndGoals: Boolean) {
        ChallengeBot.sendMessage(challenger.telegramUser.id, "\uD83D\uDCAF Herzlichen Glückwunsch! Du hast dein persönliches Ziel in der Sportart ${type.name} von $goal ${type.unit} erreicht!")

        if (!shareVideoAndGoals) return

        val message = "*${challenger.nickname.escapeMarkdown()}* hat sein persönliches Ziel von *$goal ${type.unit} ${type.title}* erreicht!"
        ChallengerManager.challengers.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message)
            } catch (e: Exception) {
            }
        }
    }
}
