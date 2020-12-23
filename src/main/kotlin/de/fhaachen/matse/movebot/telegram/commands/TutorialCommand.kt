package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.control.StatisticsManager
import de.fhaachen.matse.movebot.getRelativeTimeSpan
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.*
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.bots.AbsSender

object TutorialCommand : ChallengerCommand("tutorial", "Detaillierte Beschreibung der Funktionen dieses Bots.") {

    val tutorial = mutableListOf<String>()

    init {
        onlyUserChat()

        // TODO Videovorstellung

        // TODO Add /timeset command (?)
        tutorial += "Du willst eine neue Bewegung hinzufügen? Mit /addmovement kannst du zunächst über die Schaltflächen die Sportart auswählen. " +
                "Danach fragt dich der Bot ganz automatisch nach der Strecke oder den absolvierten Minuten. " +
                "Wenn du eine Aktivität aus der Vergangenheit nachtragen möchtest, kannst du diese darauffolgend mit /XXX zurückdatieren."

        tutorial += "Du willst einen Überblick über die Challenge bekommen? Mit /challenge kannst du deinen Fortschritt einsehen. " +
                "Dieser Befehl zeigt dir deinen Gesamtfortschritt an, deine Aktivitäten für den aktuellen Monat, deinen Gesamtfortschritt aufgeteilt nach Sportarten und deine letzten fünf Aktivitäten!\n" +
                "Du willst wissen wie weit alle Teilnehmer in der Challenge sind? Mit /competitor siehst du den Gesamtfortschritt aller im Vergleich. " +
                "Mit /leaderboard fragt dich der Bot mithilfe von Schaltflächen nach einer Sportart und lässt dich dann den Fortschritt aller Teilnehmer in einer Sportart im Vergleich sehen!"

        tutorial += "Du willst einen neuen Trainingsplan hinzufügen oder absolvierst eine Strecke regelmäßig? " +
                "Mit /newplan fragt dich der Bot mithilfe von Schaltflächen nach der Sportart und Strecke bzw. Zeit und lässt dich für das Training einen Namen vergeben. " +
                "Wenn du das Training absolvierst kannst du einfach mit /addtraining die Schaltfläche des Trainingsplans auswählen und die Aktivität wird automatisch eingetragen."

        tutorial += "Du suchst nach einem zusätzlichen Anstoß für deine Motivation? Kein Problem mit /newreminder kannst du auswählen in welchen Intervallen dich der Bot an Bewegung erinnern soll. " +
                "Wähle einfach deine gewünschte Schaltfläche aus und lass dich überraschen!"

        tutorial += "Du suchst nach einem Befehl und hast ihn bisher noch nicht gefunden? Kein Problem mit /help werden dir alle verfügbaren befehle des Bots angezeigt!\n" +
                "Zum Ausführen von Befehlen kannst du ganz einfach das / in das Nachrichtenfeld eintippen und aus dem Befehlsmenü deinen gewünschten Befehl aussuchen oder direkt komplett eintippen und absenden. " +
                "Mithilfe der / Schaltfläche neben dem Nachrichtenfeld lässt sich das Befehlsmenü auch direkt öffnen und der gewünschte Befehl durch antippen kinderleicht auswählen."

        tutorial += "*Wochenstatistik*\nJeden Sonntag wird geschaut, wer in den vergangen Tagen die meisten Punkte erzielt hat. Wer die meisten Punkte hat, gewinnt die Woche für sich!\n\n" +
                "Damit du in der Statistik auftauchst, musst du innerhalb der letzten Woche eine Aktivität erfassen.\n/${AddMovementCommand.command}\n/${AddTrainingCommand.command}"

        parameters += object : Parameter("Seite", "Gebe die Seite des Tutorials ein. (Seite 1-${tutorial.size})", optional = true) {
            override fun isValueAllowed(value: String) = value.matches(Regex("^[0-9]*$")) && value.toInt() > 0 && value.toInt() <= tutorial.size
        }
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.TUTORIAL_COMMAND)
        val page = (params.firstOrNull()?.toInt()?:1)

        val message = "($page/${tutorial.size})      *Tutorial*\n\n${tutorial[page-1]}"
        sendMessage(chat, message, inlineKeyboardFromPair(listOf("Zurück" to "$command ${page - 1}", "Weiter" to "$command ${page + 1}").filterIndexed {idx, _ -> (idx == 0 && page != 1) || (idx == 1 && page != tutorial.size) } ))
            .also { MessageHandler.addDeleteableMessage(it, MessageType.TUTORIAL) }
    }
}