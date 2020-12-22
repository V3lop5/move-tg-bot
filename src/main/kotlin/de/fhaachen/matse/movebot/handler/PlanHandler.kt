package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.PlanAdd
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Plan

object PlanHandler : PlanAdd {

    val planAddListener = mutableListOf<PlanAdd>()

    override fun onPlanAdd(challenger: Challenger, plan: Plan) {
        planAddListener.forEach { it.onPlanAdd(challenger, plan) }
    }
}