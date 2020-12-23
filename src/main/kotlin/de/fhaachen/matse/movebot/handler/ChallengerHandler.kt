package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.ChallengerAdd
import de.fhaachen.matse.movebot.handler.events.GoalSet
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType

object ChallengerHandler : ChallengerAdd, GoalSet {
    val challengerAddListener = mutableListOf<ChallengerAdd>()
    val goalSetListener = mutableListOf<GoalSet>()

    override fun onChallengerAdd(challenger: Challenger) {
        challengerAddListener.forEach { it.onChallengerAdd(challenger) }
    }

    override fun onGoalSet(challenger: Challenger, movementType: MovementType, goal: Int) {
        goalSetListener.forEach { it.onGoalSet(challenger, movementType, goal) }
    }
}