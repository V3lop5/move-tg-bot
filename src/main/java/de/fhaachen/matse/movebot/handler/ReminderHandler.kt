package de.fhaachen.matse.movebot.handler

import de.fhaachen.matse.movebot.handler.events.ReminderAdd
import de.fhaachen.matse.movebot.handler.events.ReminderSend
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Reminder

object ReminderHandler : ReminderAdd, ReminderSend {

    val reminderAddListener = mutableListOf<ReminderAdd>()
    val reminderSendListener = mutableListOf<ReminderSend>()

    override fun onReminderAdd(challenger: Challenger, reminder: Reminder) {
        reminderAddListener.forEach { it.onReminderAdd(challenger, reminder) }
    }


    override fun onReminderSend(challenger: Challenger, reminder: Reminder) {
        reminderSendListener.forEach { it.onReminderSend(challenger, reminder) }
    }
}