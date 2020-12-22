package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Reminder
import de.fhaachen.matse.movebot.model.ReminderType
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.telegram.model.AllowedValuesParameter
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.*

object NewReminderCommand : ChallengerCommand("newreminder", "Du denkst selber nicht ans Eintragen deiner Strecken? Lege eine persönliche Erinnerung fest!") {
    private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm", Locale.GERMAN)

    init {
        parameters.add(AllowedValuesParameter("Intervall", "Wann möchtest du erinnert werden?", ReminderType.values().map { it.name }))

        parameters.add(object : Parameter("Uhrzeit", "Gebe die Uhrzeit ein. (hh:mm oder 9 für 9:00)") {
            override fun isValueAllowed(value: String) = parseTime(value) != null
        })

    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val reminderType = ReminderType.valueOf(params[0])
        val reminderTime = parseTime(params[1])!!

        val reminder = Reminder(reminderType, reminderTime)

        if (challenger.reminders.contains(reminder)) {
            sendComplete(chat, "Es existiert bereits eine Erinnerung zu den angegeben Einstellungen. Die Erinnerung wurde nicht erneut hinzugefügt.")
            return
        }

        challenger.addReminder(reminder)
        sendComplete(chat, "Die Erinnerung wurde hinzugefügt! \n" +
                "Typ: *$reminderType*\n" +
                "Uhrzeit: *${reminderTime.prettyString()}*")
    }


    // TODO Verbessern
    fun parseTime(value: String): LocalTime? {

        val singleHour = value.toIntOrNull()
        if (singleHour != null && singleHour in (0..24)) {
            return LocalTime.of(singleHour, 0)
        }

        return try {
            LocalTime.parse(value, timeFormatter)
        } catch (e: Exception) {
            //   e.printStackTrace()
            null
        }
    }
}