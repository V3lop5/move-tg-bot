package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.POINT_GOAL
import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.telegram.commands.TutorialCommand
import java.time.LocalDate

val userchatRequirement = Requirement("Dieser Befehl kann nur im privaten Chat ausgeführt werden.") { _, chat, _ -> chat.isUserChat }

val inYearRequirement = Requirement("Dieser Befehl kann nur im Jahr $POINT_GOAL genutzt werden. Bitte gedulde dich noch, bis der Bot auf das neue Jahr vorbereitet ist!") { _, _, _ -> LocalDate.now().year == POINT_GOAL }

val anyActivityRequirement = Requirement("Dieser Befehl kann nur genutzt werden, wenn du mindestens eine Aktivität erfasst hast!") { user, _, _ -> ChallengerManager.findChallenger(user)?.run { movements.isNotEmpty() }?:false }

val allowPersonalShareRequirement = Requirement("Dieser Befehl kann nur genutzt werden, wenn du selber ein Vorstellungsvideo aufgenommen hast und dieses mit den anderen Teilnehmern teilst.\n\nNehme ein Video auf und schicke es mir. Mehr dazu: /tutorial") { user, _, _ -> ChallengerManager.findChallenger(user)?.run { shareVideoAndGoals && isVideoAccepted }?:false }

val notSuspiciousRequirement = Requirement("Du wirst verdächtigt falsche Angaben zu tätigen. Solange der Verdacht besteht, darfst du diesen Befehl nicht nutzen. Bitte setze dich mit uns in Verbindung, um den Irrtum auszuräumen. /feedback")
{ user, _, _ -> ChallengerManager.findChallenger(user)?.run { !suspicious }?:false }
