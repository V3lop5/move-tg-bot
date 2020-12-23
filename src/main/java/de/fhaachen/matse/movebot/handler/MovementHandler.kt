package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.MovementAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement

object MovementHandler : MovementAdd {

    val movementAddListener = mutableListOf<MovementAdd>()

    override fun onMovementAdd(challenger: Challenger, movement: Movement) {
        movementAddListener.forEach { it.onMovementAdd(challenger, movement) }
    }
}