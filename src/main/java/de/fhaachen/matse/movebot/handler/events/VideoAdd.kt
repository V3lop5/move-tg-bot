package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType

interface VideoAdd {
    fun onVideoAdd(challenger: Challenger)
}