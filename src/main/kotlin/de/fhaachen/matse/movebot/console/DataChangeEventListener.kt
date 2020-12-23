package de.fhaachen.matse.movebot.console

import de.fhaachen.matse.movebot.handler.ChallengerHandler
import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.PlanHandler
import de.fhaachen.matse.movebot.handler.ReminderHandler
import de.fhaachen.matse.movebot.handler.events.*
import de.fhaachen.matse.movebot.lastDataChange
import de.fhaachen.matse.movebot.model.*
import java.time.LocalDateTime

object DataChangeEventListener {

    fun register() {
        println("DataChangeEventListener wird gestartet...")


        ChallengerHandler.challengerAddListener += object : ChallengerAdd {
            override fun onChallengerAdd(challenger: Challenger) {
                lastDataChange = LocalDateTime.now()
            }
        }

        ChallengerHandler.goalSetListener += object : GoalSet {
            override fun onGoalSet(challenger: Challenger, movementType: MovementType, goal: Int) {
                lastDataChange = LocalDateTime.now()
            }
        }

        MovementHandler.movementAddListener += object : MovementAdd {
            override fun onMovementAdd(challenger: Challenger, movement: Movement) {
                lastDataChange = LocalDateTime.now()
            }
        }

        PlanHandler.planAddListener += object : PlanAdd {
            override fun onPlanAdd(challenger: Challenger, plan: Plan) {
                lastDataChange = LocalDateTime.now()
            }
        }

        ReminderHandler.reminderAddListener += object : ReminderAdd {
            override fun onReminderAdd(challenger: Challenger, reminder: Reminder) {
                lastDataChange = LocalDateTime.now()
            }
        }

        ReminderHandler.reminderSendListener += object : ReminderSend {
            override fun onReminderSend(challenger: Challenger, reminder: Reminder) {
                lastDataChange = LocalDateTime.now()
            }
        }
    }
}