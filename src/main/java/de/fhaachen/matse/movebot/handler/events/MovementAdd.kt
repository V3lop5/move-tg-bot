package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement

interface MovementAdd {

    fun onMovementAdd(challenger: Challenger, movement: Movement)
}