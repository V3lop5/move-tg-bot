package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.events.MovementAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import java.time.Duration

object Challenge2020Complete : MovementAdd {


    fun register() {
        MovementHandler.movementAddListener += this
    }

    override fun onMovementAdd(challenger: Challenger, movement: Movement) {
        val newDistance = StatisticsManager.getDistance(challenger)
        val oldDistance = newDistance - movement.distance

        if (newDistance < 2020 || oldDistance >= 2020)
            return

        complete(challenger)
    }

    fun complete(challenger: Challenger) {
        ChallengeBot.sendMessage(challenger.telegramUser.id, "=============================\n" +
                "Glükwunsch! Du hast die 2020!\n" +
                "=============================\n\n" +
                "In ${challenger.movements.count()} Aktivitäten hast du durchschnittlich ${challenger.movements.map { it.distance }.average().round(2)} Kilometer zurückgelegt." +
                "Zwischen der ersten und der neusten Aktivität lagen ${Duration.between(challenger.movements.map { it.datetime }.min(), challenger.movements.map { it.datetime }.max()).toDays()} Tage.\n\n" +
                "Danke für deine Teilnahme! Bist du auch bei der nächsten Challenge dabei? ;)")

        val message = "*${challenger.nickname}* hat die Move! - Die Challenge 2021 geschafft!"
        ChallengerManager.challengers.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message)
            } catch (e: Exception) {
            }
        }
    }
}
