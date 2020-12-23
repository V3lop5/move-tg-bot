package de.fhaachen.matse.movebot.handler.events

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Reminder

interface ReminderSend {

    fun onReminderSend(challenger: Challenger, reminder: Reminder)
}