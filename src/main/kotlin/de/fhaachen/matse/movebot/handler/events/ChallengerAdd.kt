package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger

interface ChallengerAdd {
    fun onChallengerAdd(challenger: Challenger)
}