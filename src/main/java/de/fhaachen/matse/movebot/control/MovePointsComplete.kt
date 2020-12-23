package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.POINT_GOAL
import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.events.MovementAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import java.time.Duration

object MovePointsComplete : MovementAdd {


    fun register() {
        MovementHandler.movementAddListener += this
    }

    override fun onMovementAdd(challenger: Challenger, movement: Movement) {
        val newDistance = StatisticsManager.getPoints(challenger)
        val oldDistance = newDistance - movement.points

        if (newDistance < POINT_GOAL || oldDistance >= POINT_GOAL)
            return

        complete(challenger)
    }

    fun complete(challenger: Challenger) {
        ChallengeBot.sendMessage(challenger.telegramUser.id, "=============================\n" +
                "Glükwunsch! Du hast die $POINT_GOAL Punkte!\n" +
                "=============================\n\n" +
                "In ${challenger.movements.count()} Aktivitäten hast du durchschnittlich ${challenger.movements.map { it.points }.average().round(2)} Punkte erzielt." +
                "Zwischen der ersten und der neusten Aktivität lagen ${Duration.between(challenger.movements.map { it.datetime }.min(), challenger.movements.map { it.datetime }.max()).toDays()} Tage.\n\n" +
                "Danke für deine Teilnahme! Bist du auch bei der nächsten Challenge dabei?")

        val message = "*${challenger.nickname}* hat $POINT_GOAL Punkte erreicht!"
        ChallengerManager.challengers.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message)
            } catch (e: Exception) {
            }
        }
    }
}
