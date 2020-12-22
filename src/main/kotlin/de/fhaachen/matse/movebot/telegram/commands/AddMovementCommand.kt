package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ConfirmHandler
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.distanceParameter
import de.fhaachen.matse.movebot.telegram.model.movementTypeParameter
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

object AddMovementCommand : ChallengerCommand("addmovement", "Du hast dich bewegt? Perfekt! Erfasse hiermit deine 'Leistung'!") {
    private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.GERMAN)

    init {
        parameters.add(movementTypeParameter)
        parameters.add(distanceParameter)

        parameters.add(object : Parameter("Datum", "Gebe das Datum ein. (heute, gestern, vorgestern, vor x Tagen oder dd.MM.yyyy)") {
            override fun isValueAllowed(value: String) = parseDate(value) != null && parseDate(value)?.isBefore(LocalDateTime.now().plusMinutes(1)) ?: false
        })
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.valueOf(params[0])
        val distance = params[1].toDouble().round(2)
        val datetime = parseDate(params[2]) ?: LocalDateTime.now()

        val movement = Movement(datetime, movementType, distance)

        if (challenger.hasSameMovementAtThisDay(movement)) {
            if (!ConfirmHandler.hasPendingConfirmation(chat)) {
                ConfirmHandler.requestConfirmation(chat, "Du hast zu dem Datum ${movement.datetime.prettyDateString()} bereits die Strecke (${movement.type} / *${movement.distance} km* erfasst." +
                        "\nHast du diese Strecke an dem Tag doppelt zurückgelegt?") { handle(sender, user, chat, challenger, params) }
                return
            }

            if (!ConfirmHandler.hasConfirmed(chat.id)) {
                sendComplete(chat, "Die Strecke wurde nicht hinzugefügt, da diese bereits gespeichert ist.")
                return
            }
        } else if (datetime.isBefore(LocalDateTime.now().minusDays(7))) {
            if (!ConfirmHandler.hasPendingConfirmation(chat)) {
                ConfirmHandler.requestConfirmation(chat, "Du erfasst gerade eine Bewegung zum ${datetime.prettyDateString()}. Das Datum liegt schon über eine Woche zurück. Bist du dir sicher?") { handle(sender, user, chat, challenger, params) }
                return
            }

            if (!ConfirmHandler.hasConfirmed(chat.id)) {
                sendComplete(chat, "Die Strecke wurde nicht hinzugefügt, da diese in der Vergangenheit liegt..")
                return
            }
        }


        challenger.addMovement(movement)
        sendComplete(chat, "Die Strecke wurde hinzugefügt!\nTyp: *$movementType*\nStrecke: *$distance Kilometer*\nDatum: ${datetime.prettyString()}\nDeine Übersicht: /${ChallengeCommand.command}")

    }

    fun parseDate(value: String): LocalDateTime? {
        if (value.equals("heute", true)) return LocalDateTime.now()
        if (value.equals("gestern", true)) return LocalDateTime.now().minusDays(1)
        if (value.equals("vorgestern", true)) return LocalDateTime.now().minusDays(2)

        val beforeXDays = value.toLongOrNull()
        if (beforeXDays != null)
            return LocalDateTime.now().minusDays(beforeXDays)

        return try {
            LocalDate.parse(value, dateFormatter).atStartOfDay()
        } catch (e: Exception) {
            // e.printStackTrace()
            null
        }
    }
}