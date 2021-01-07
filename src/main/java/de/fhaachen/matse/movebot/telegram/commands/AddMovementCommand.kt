package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.Movement
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.prettyString
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.ConfirmHandler
import de.fhaachen.matse.movebot.telegram.model.*
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
        requirements += inYearRequirement
        requirements += notSuspiciousRequirement

        parameters.add(movementTypeParameter)
        parameters.add(movementValueParameter)

        parameters.add(object : Parameter("Datum", "Gebe das Datum ein. (heute, gestern, vorgestern, vor x Tagen oder dd.MM.yyyy)", optional = true) {
            override fun isValueAllowed(value: String) = parseDate(value) != null && parseDate(value)?.isBefore(LocalDateTime.now().plusMinutes(1)) ?: false
        })
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        val movementType = MovementType.of(params[0])
        val distance = params[1].toDouble().round(2)
        val datetime = params.getOrNull(2)?.let(this::parseDate) ?: LocalDateTime.now()

        val movement = Movement(datetime, movementType, distance)

        if (challenger.hasSameMovementAtThisDay(movement)) {
            if (!ConfirmHandler.hasPendingConfirmation(chat)) {
                ConfirmHandler.requestConfirmation(chat, "Du hast zu dem Datum ${movement.datetime.prettyDateString()} bereits die Aktivität ${movement.type} mit *${movement.value} ${movement.type.unit}* erfasst." +
                        "\nHast du diese Aktivität an dem Tag zweimal gemacht?") { handle(sender, user, chat, challenger, params) }
                return
            }

            if (!ConfirmHandler.hasConfirmed(chat.id)) {
                sendComplete(chat, "Die Aktivität wurde nicht hinzugefügt, da diese bereits gespeichert ist.")
                return
            }
        } else if (datetime.isBefore(LocalDateTime.now().minusDays(7))) {
            if (!ConfirmHandler.hasPendingConfirmation(chat)) {
                ConfirmHandler.requestConfirmation(chat, "Du erfasst gerade eine Aktivität zum ${datetime.prettyDateString()}. Das Datum liegt schon über eine Woche zurück. Bist du dir sicher?") { handle(sender, user, chat, challenger, params) }
                return
            }

            if (!ConfirmHandler.hasConfirmed(chat.id)) {
                sendComplete(chat, "Die Aktivität wurde nicht hinzugefügt, da diese in der Vergangenheit liegt..")
                return
            }
        }


        challenger.addMovement(movement)
        sendComplete(chat, "${movement.type.emoji} *${movement.value} ${movement.type.unit} ${movement.type.title}* erfasst!\nDafür gibt's ${movement.points.round(2)} Punkte!\nPrüfe deinen Fortschritt: /${ChallengeCommand.command}")

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