package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.prettyDateString
import de.fhaachen.matse.movebot.telegram.ConfirmHandler
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.allowPersonalShareRequirement
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object AddTrainingCommand : ChallengerCommand("addtraining", "Training ist wieder geschafft. Jetzt nur noch schnell die Kilometer festhalten. Hiermit kannst du es ganz bequem per Plan machen.") {
    init {
        requirements += allowPersonalShareRequirement

        parameters.add(Parameter("Name des Plans", "Welchen Plan möchtest du hinzufügen?", optional = true))
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        if (challenger.plans.isEmpty()) {
            sendMessage(chat, "Du hast noch keinen Plan angelegt. Ein Plan dient dazu, um wiederkehrende Aktivitäten schnell zu erfassen.\nNutze /${NewPlanCommand.command}")
            return
        }

        if (params.isEmpty()) {
            sendMessage(chat, "Wähle einen deiner Pläne aus!", inlineKeyboardFromPair(challenger.plans.map { Pair(it.keyword, "$command ${it.keyword}") }))
            return
        }

        val plan = challenger.plans.find { it.keyword.equals(params[0], ignoreCase = true) }

        if (plan == null) {
            sendMessage(chat, "Der Plan ${params[0]} existiert nicht. Bitte wähle einen anderen Plan!", inlineKeyboardFromPair(challenger.plans.map { Pair(it.keyword, "$command ${it.keyword}") }))
            return
        }

        val movement = plan.toMovement()

        if (challenger.hasSameMovementAtThisDay(movement)) {
            if (!ConfirmHandler.hasPendingConfirmation(chat)) {
                ConfirmHandler.requestConfirmation(chat, "Du hast zu dem Datum ${movement.datetime.prettyDateString()} bereits die Strecke (${movement.type} / *${movement.value} km* erfasst." +
                        "\nHast du diese Strecke an dem Tag doppelt zurückgelegt?") { handle(sender, user, chat, challenger, params) }
                return
            }

            if (!ConfirmHandler.hasConfirmed(chat.id)) {
                sendComplete(chat, "Das Training ${plan.keyword} wurde nicht hinzugefügt, da es bereits zum gleichen Tag erfasst wurde.")
                return
            }
        }

        challenger.addMovement(movement)

        sendComplete(chat, "Die Strecke wurde anhand des Plans *${plan.keyword}* hinzugefügt.\n${movement.type} // *${movement.value} km* // ${plan.description}")
    }
}