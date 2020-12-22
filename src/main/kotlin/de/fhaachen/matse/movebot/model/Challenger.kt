package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.handler.MovementHandler
import de.fhaachen.matse.movebot.handler.PlanHandler
import de.fhaachen.matse.movebot.handler.ReminderHandler
import de.fhaachen.matse.movebot.telegram.getName
import org.telegram.telegrambots.meta.api.objects.User

data class Challenger(val telegramUser: User) {

    private val permissions = mutableListOf<ChallengerPermission>()
    var customNickname: String? = null


    val movements = mutableListOf<Movement>()
    val reminders = mutableListOf<Reminder>()
    val plans = mutableListOf<Plan>()

    val nickname: String
        get() = if (customNickname != null) customNickname!! else telegramUser.getName() ?: "(ID: ${telegramUser.id})"


    fun addMovement(movement: Movement) {
        movements += movement
        MovementHandler.onMovementAdd(this, movement)
    }

    fun hasSameMovementAtThisDay(movement: Movement): Boolean {
        return movements.any { it.datetime.dayOfYear == movement.datetime.dayOfYear && it.type == movement.type && it.distance == movement.distance }
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

}