package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.ChallengerAdd
import de.fhaachen.matse.movebot.model.Challenger

object ChallengerHandler : ChallengerAdd {
    val challengerAddListener = mutableListOf<ChallengerAdd>()

    override fun onChallengerAdd(challenger: Challenger) {
        challengerAddListener.forEach { it.onChallengerAdd(challenger) }
    }
}