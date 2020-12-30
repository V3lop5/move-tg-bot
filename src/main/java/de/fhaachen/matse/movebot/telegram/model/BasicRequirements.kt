package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.telegram.commands.TutorialCommand
import java.time.LocalDate

val userchatRequirement = Requirement("Dieser Befehl kann nur im privaten Chat ausgeführt werden.") { _, chat, _ -> chat.isUserChat }

val inYearRequirement = Requirement("Dieser Befehl kann nur im Jahr 2021 genutzt werden. Bitte gedulde dich noch!") { _, _, _ -> LocalDate.now().year == 2021 }

val allowPersonalShareRequirement = Requirement("Dieser Befehl kann nur genutzt werden, wenn du selber ein Vorstellungsvideo aufgenommen hast und dieses mit den anderen Teilnehmern teilst.\n\nNehme ein Video auf und schicke es mir. Mehr dazu: /tutorial") { user, _, _ -> ChallengerManager.findChallenger(user)?.run { shareVideoAndGoals && isVideoAccepted }?:false }
