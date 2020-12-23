package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Plan

interface PlanAdd {

    fun onPlanAdd(challenger: Challenger, plan: Plan)
}