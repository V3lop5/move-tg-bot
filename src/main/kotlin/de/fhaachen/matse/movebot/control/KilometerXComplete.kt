package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.events.MovementAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.telegram.ChallengeBot

object KilometerXComplete : MovementAdd {

    val kilometers = listOf(1010, 4040, 5000, 6060, 6666)


    fun register() {
        MovementHandler.movementAddListener += this
    }

    override fun onMovementAdd(challenger: Challenger, movement: Movement) {
        val newDistance = StatisticsManager.getDistance(challenger)
        val oldDistance = newDistance - movement.distance

        kilometers.forEach {
            if (newDistance < it || oldDistance >= it)
                return

            complete(challenger, it)
        }
    }

    fun complete(challenger: Challenger, it: Int) {
        val message = "*${challenger.nickname}* hat $it Kilometer geschafft!"
        ChallengerManager.challengers.forEach {
            try {
                ChallengeBot.sendMessage(it.telegramUser.id, message)
            } catch (e: Exception) {
            }
        }
    }
}
