package de.fhaachen.matse.movebot.console

import de.fhaachen.matse.movebot.handler.*
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

        ChallengerHandler.videoAddListener += object : VideoAdd {
            override fun onVideoAdd(challenger: Challenger) {
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


        TeamHandler.teamCreatedListener += object : TeamCreated {
            override fun onTeamCreation(challenger: Challenger, team: Team) {
                lastDataChange = LocalDateTime.now()
            }
        }

        TeamHandler.teamJoinedListener += object : TeamJoined {
            override fun onTeamJoin(challenger: Challenger, team: Team) {
                lastDataChange = LocalDateTime.now()
            }
        }
    }
}