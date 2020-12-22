package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.handler.ReminderHandler
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Reminder
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.commands.AddMovementCommand
import de.fhaachen.matse.movebot.telegram.commands.AddTrainingCommand
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import java.time.LocalDate
import java.time.LocalDateTime

object ReminderManager {

    fun checkRemindersFromChallengers() {
        ChallengerManager.challengers.forEach { challenger ->
            challenger.reminders.filter { it.isPending() }.forEach { sendReminder(challenger, it) }
        }
    }

    private fun sendReminder(challenger: Challenger, reminder: Reminder) {
        val dateString = LocalDateTime.now().prettyDateString()
        val message = "Deine Erinnerung *${reminder.reminderType.name} / ${reminder.reminderTime.prettyString()}*:\nWelche Strecke hast du heute ($dateString) zurückgelegt?"
        val keyboard = inlineKeyboardFromPair(challenger.plans.map { Pair(it.keyword, "#reminder ${AddTrainingCommand.command} ${it.keyword} $dateString") }
                .union(listOf(Pair("Aktivität erfassen", "#reminder ${AddMovementCommand.command} <REQUEST> <REQUEST> $dateString"), Pair("Ich war faul!", "#reminder"))).toList())
        try {
            MessageHandler.cleanupMessages(challenger.telegramUser.id.toLong(), MessageCleanupCause.NEW_REMINDER)
            ChallengeBot.sendMessage(challenger.telegramUser.id, message, keyboard).also { MessageHandler.addDeleteableMessage(it, MessageType.REMINDER) }
            reminder.lastExecution = LocalDate.now()
            ReminderHandler.onReminderSend(challenger, reminder)
        } catch (e: Exception) {
            System.err.println("Reminder für Challenger ${challenger.nickname} (${challenger.telegramUser.id}) konnte nicht gesendet werden.")
        }
    }
}