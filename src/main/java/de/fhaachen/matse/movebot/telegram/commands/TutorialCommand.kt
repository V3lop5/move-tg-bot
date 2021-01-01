package de.fhaachen.matse.movebot.telegram.commands

import de.fhaachen.matse.movebot.POINT_GOAL
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.round
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.ChallengerCommand
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.Parameter
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Chat
import org.telegram.telegrambots.meta.api.objects.User
import org.telegram.telegrambots.meta.bots.AbsSender

object TutorialCommand : ChallengerCommand("tutorial", "Detaillierte Beschreibung der Funktionen dieses Bots.") {

    val tutorial = mutableListOf<String>()

    init {
        onlyUserChat()

        tutorial += "*Pulse on Fire - Die Challenge*\n\nSchaffst du im Jahr $POINT_GOAL $POINT_GOAL Punkte?\nDieser Bot ermöglicht es dir deine sportlichen Aktivitäten zu erfassen. " +
                "Die verschiedenen Sportarten geben hierbei unterschiedlich viele Punkte. Für 1 Punkt brauchst du:\n" +
                "${
                    MovementType.values().sortedBy { it.title }.joinToString(
                        prefix = "- ",
                        separator = "\n- "
                    ) { "${it.emoji}   ${(1 / it.pointsPerUnit).round(1)} ${it.unit} ${it.title}" }
                }\n\n" +
                "Zusätzlich kannst du dich mit den anderen Teilnehmern der Challenge vergleichen. Sieh die Wochenstatistik als Motivation!"

        tutorial += "*Persönliche Jahresziele*\n\nDu kannst dir neben dem Ziel von *$POINT_GOAL Punkten* weitere persönliche Ziele festlegen. " +
                "Je Sportart kannst du eine Distanz, Aktivitätsdauer oder Anzahl an Wiederholungen festlegen.\n" +
                "Nutze dafür den Befehl /${GoalCommand.command}. Doch Achtung! Du kannst deine Ziele nachträglich nicht mehr verändern!\n\n" +
                "Falls du deine persönlichen Ziele mit den anderen Teilnehmern der Challenge teilen möchtest, lade doch ein kurzes Vorstellungsvideo hoch.\n» Mehr auf der nächsten Seite."

        tutorial += "*Vorstellungsvideo*\n\nDies ist freiwillig!\nWenn du die anderen Challenge-Teilnehmer kennen lernen möchtest, lade doch ein kurzes Vorstellungsvideo von dir hoch. Sende mir (dem Bot) ein kurzes Video und bestätige, dass die anderen Teilnehmer das Video sehen dürfen.\n\n" +
                "Anschließend kannst du mit dem Befehl /${WhoisCommand.command} die persönlichen Ziele und das Vorstellungsvideo von anderen Teilnehmern ansehen. Es wurden bereits von über ${ChallengerManager.challengers.count { it.shareVideoAndGoals }} Teilnehmern Videos hochgeladen."

        tutorial += "*Aktivitäten erfassen*\n\nMithilfe des Befehls /${AddMovementCommand.command} kannst du deine sportlichen Aktivitäten erfassen." +
                "Nach Aufruf des Befehls kannst du über die Schaltflächen die Sportart auswählen und anschließend die Distanz, Dauer oder Anzahl der Wiederholungen eingeben. Fertig!\n\n" +
                "Du kannst auch Trainingspläne erstellen und eine automatische Erinnerung erhalten, damit die Erfassung noch einfacher ist.\n» Diese Funktionen sind auf den folgenden Seiten erklärt."

        tutorial += "*Fortschritt der Challenge*\n\nMithilfe des Befehls /${ChallengeCommand.command} kannst du deinen Fortschritt bei der Challenge einsehen. " +
                "Es zeigt deine aktuelle Punktezahl, den Fortschritt bei deinen persönlichen Zielen, den Fortschritt je Aktivität und deine zuletzt erfassten Aktivitäten an.\n\n" +
                "Mit dem Befehl /${CompetitorCommand.command} kannst du deine Punktzahl mit den anderen Teilnehmern vergleichen. Vielleicht kannst du ja jemanden überholen? \uD83D\uDE09 \n\n" +
                "Der Befehl /${LeaderboardCommand.command} zeigt je Sportart den Fortschritt aller Teilnehmer an. Vielleicht schaffst du bei einer Sportart Platz 1?"

        tutorial += "*Trainingspläne*\n\nDu willst einen neuen Trainingsplan hinzufügen oder absolvierst eine Strecke regelmäßig? " +
                "Mit /${NewPlanCommand.command} fragt dich der Bot mithilfe von Schaltflächen nach der Sportart und Strecke bzw. Zeit und lässt dich für das Training einen Namen vergeben.\n\n" +
                "Wenn du das Training absolvierst kannst du einfach mit /${AddTrainingCommand.command} die Schaltfläche des Trainingsplans auswählen und die Aktivität wird automatisch eingetragen."

        tutorial += "*Erinnerungen*\n\nDu suchst nach einem zusätzlichen Anstoß für deine Motivation? " +
                "Kein Problem mit /${NewReminderCommand.command} kannst du auswählen in welchen Intervallen (Täglich oder Werktags) dich der Bot die Erfassung von Aktivitäten erinnern soll."

        tutorial += "*Wochenstatistik*\n\nJeden Sonntag wird geschaut, wer in den vergangen Tagen die meisten Punkte erzielt hat. Der Teilnehmer mit den meisten Punkten gewinnt die Woche für sich!\n\n" +
                "Damit du in dieser Statistik auftauchst, musst du innerhalb der letzten Woche mindestens eine Aktivität erfasst haben.\nNutze dafür /${AddMovementCommand.command}"

        tutorial += "*Teams und Teamchallenges*\n\nIn diesem Jahr kannst du dich mit anderen Teilnehmern zu Teams zusammenschließen. Zum Anlegen eines Teams nutze den Befehl /${NewTeamCommand.command} und zum Beitritt den Befehl /${JoinTeamCommand.command}.\n\n" +
                "Unabhängig von der allgemeinen Challenge können Teams untereinander Teamkämpfe austragen. Mithilfe des Befehls /${NewTeamFightCommand.command} kannst du ein anderes Team in einer Sportart herausfordern. Der Befehl /${TeamFightCommand.command} zeigt dir eine Übersicht aller Teamkämpfe, an denen du beteiligt bist.\n\n" +
                "Der Befehl /${TeamCommand.command} zeigt die Informationen über ein Team an."

        tutorial += "*Hilfestellung*\n\nDu suchst nach einem Befehl und hast ihn bisher noch nicht gefunden? Kein Problem mit /help werden dir alle verfügbaren Befehle angezeigt!\n" +
                "Zum Ausführen eines bestimmten Befehls kannst du / gefolgt von dem Namen des Befehls in das Nachrichtenfeld eingeben. Alternativ kannst du auch nur `/` eingeben und aus dem Menü den gesuchten Befehl auswählen." +
                "Mithilfe der / Schaltfläche rechts neben dem Nachrichtenfeld kannst du das Befehlsmenü auch direkt öffnen. Als weitere Möglichkeit kannst du auch direkt auf die blau hinterlegten Befehle in den Nachrichten tippen. Probier es am Besten direkt aus.\n\n" +
                "Du kannst jederzeit die Befehlsführung mit /cancel abbrechen.\n\n" +
                "Falls du einmal zusätzliche Hilfe brauchst oder Feedback hast, nutze den Befehl /${FeedbackCommand.command}. Ich (Paul) trete dann mit dir in Verbindung.\nViel Erfolg bei der Challenge! Wir sehen uns in der Wochenstatistik!"


        parameters += object :
            Parameter("Seite", "Gebe die Seite des Tutorials ein. (Seite 1-${tutorial.size})", optional = true) {
            override fun isValueAllowed(value: String) =
                value.matches(Regex("^[0-9]*$")) && value.toInt() > 0 && value.toInt() <= tutorial.size
        }
    }

    override fun handle(sender: AbsSender, user: User, chat: Chat, challenger: Challenger, params: List<String>) {
        MessageHandler.cleanupMessages(chat.id, MessageCleanupCause.COMMAND_CANCELED)
        val page = (params.firstOrNull()?.toInt() ?: 1)

        val message = "($page/${tutorial.size})      *Tutorial*  -  ${tutorial[page - 1]}"
        sendMessage(
            chat,
            message,
            inlineKeyboardFromPair(
                listOf(
                    "Zurück" to "$command ${page - 1}",
                    "Weiter" to "$command ${page + 1}"
                ).filterIndexed { idx, _ -> (idx == 0 && page != 1) || (idx == 1 && page != tutorial.size) }))
    }
}