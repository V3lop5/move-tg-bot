package de.fhaachen.matse.movebot.console

import de.fhaachen.matse.movebot.handler.*
import de.fhaachen.matse.movebot.handler.events.*
import de.fhaachen.matse.movebot.model.*
import de.fhaachen.matse.movebot.prettyString
import java.time.LocalTime

object ConsoleEventListener {

    fun register() {
        println("Console wird gestartet...")


        ChallengerHandler.challengerAddListener += object : ChallengerAdd {
            override fun onChallengerAdd(challenger: Challenger) {
                println("[onChallengerAdd] Neuer Challenger ${challenger.nickname} (${challenger.telegramUser.id})")
            }
        }

        ChallengerHandler.goalSetListener += object : GoalSet {
            override fun onGoalSet(challenger: Challenger, movementType: MovementType, goal: Int) {
                println("[onGoalSet] Challenger ${challenger.nickname} (${challenger.telegramUser.id}) hat sein Ziel für Sportart ${movementType.name} auf $goal ${movementType.unit} gesetzt.")
            }
        }

        ChallengerHandler.videoAddListener += object : VideoAdd {
            override fun onVideoAdd(challenger: Challenger) {
                println("[onGoalSet] Challenger ${challenger.nickname} (${challenger.telegramUser.id}) hat ein Video hinzugefügt.")
            }
        }

        MovementHandler.movementAddListener += object : MovementAdd {
            override fun onMovementAdd(challenger: Challenger, movement: Movement) =
                    println("[onMovementAdd] Bewegung für ${challenger.nickname} hinzugefügt: $movement")
        }

        PlanHandler.planAddListener += object : PlanAdd {
            override fun onPlanAdd(challenger: Challenger, plan: Plan) =
                    println("[onMovementAdd] Plan für ${challenger.nickname} hinzugefügt: $plan")
        }

        ReminderHandler.reminderAddListener += object : ReminderAdd {
            override fun onReminderAdd(challenger: Challenger, reminder: Reminder) =
                    println("[onReminderAdd] Reminder für ${challenger.nickname} hinzugefügt: $reminder")
        }


        ReminderHandler.reminderSendListener += object : ReminderSend {
            override fun onReminderSend(challenger: Challenger, reminder: Reminder) =
                    println("[onReminderSend] Erinnerung an ${challenger.nickname} um ${LocalTime.now().prettyString()} gesendet. $reminder")
        }


        TeamHandler.teamCreatedListener += object : TeamCreated {
            override fun onTeamCreation(challenger: Challenger, team: Team) =
                println("[onTeamCreation] Team ${team.name} (ID ${team.teamId}) um ${LocalTime.now().prettyString()} gesendet. Team wurde erstellt von ${challenger.nickname}")
        }

        TeamHandler.teamJoinedListener += object : TeamJoined {
            override fun onTeamJoin(challenger: Challenger, team: Team) =
                println("[onTeamCreation] User ${challenger.nickname} ist dem Team ${team.name} (ID ${team.teamId}) beigetreten.")
        }
    }
}