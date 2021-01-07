package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.handler.ChallengerHandler
import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.PlanHandler
import de.fhaachen.matse.movebot.handler.ReminderHandler
import de.fhaachen.matse.movebot.telegram.getName
import org.telegram.telegrambots.meta.api.objects.User
import java.time.LocalDate
import java.time.LocalDateTime

data class Challenger(val telegramUser: User) {

    private val permissions = mutableListOf<ChallengerPermission>()
    var customNickname: String? = null
    val joinTimestamp = LocalDateTime.now()
    var presentationVideoId: String? = null
    var shareVideoAndGoals: Boolean = false
    var isVideoAccepted: Boolean = false
    var suspicious: Boolean = false

    val movements = mutableListOf<Movement>()
    val reminders = mutableListOf<Reminder>()
    val plans = mutableListOf<Plan>()
    val goals = mutableMapOf<MovementType, Int>()

    val nickname: String
        get() = if (customNickname != null) customNickname!! else telegramUser.getName() ?: "Unknown (ID: ${telegramUser.id})"


    fun addMovement(movement: Movement) {
        movements += movement
        MovementHandler.onMovementAdd(this, movement)
    }

    fun hasSameMovementAtThisDay(movement: Movement): Boolean {
        return movements.any { it.datetime.dayOfYear == movement.datetime.dayOfYear && it.type == movement.type && it.value == movement.value }
    }


    fun promote(perm: ChallengerPermission) = permissions.add(perm)
    fun demote(perm: ChallengerPermission) = permissions.remove(perm)

    fun hasPermission(perm: ChallengerPermission): Boolean {
        if (permissions.contains(perm)) return true
        if (perm.parentPermission() != perm) return hasPermission(perm.parentPermission())
        return false
    }


    fun addPlan(plan: Plan) {
        plans += plan
        PlanHandler.onPlanAdd(this, plan)
    }

    fun addReminder(reminder: Reminder) {
        reminders += reminder
        ReminderHandler.onReminderAdd(this, reminder)
    }

    fun setGoal(movementType: MovementType, goal: Int) {
        goals[movementType] = goal
        ChallengerHandler.onGoalSet(this, movementType, goal)
    }

    fun canChangeGoal(movementType: MovementType): Boolean {
        val modifyUntil = maxOf(LocalDate.ofYearDay(2021, 3).atStartOfDay(), joinTimestamp.plusDays(2))
        return LocalDateTime.now().isBefore(modifyUntil) || !goals.containsKey(movementType)
    }


}