package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.control.ChallengerManager
import de.fhaachen.matse.movebot.model.ChallengerPermission

val challengerPermission = CommandPermission("Du benötigst ein Account. Nutze /start") { user, chat -> ChallengerManager.findChallenger(user) != null }

val adminPermission = CommandPermission("Du benötigst die Berechtigung Admin!") { user, chat -> ChallengerManager.findChallenger(user)?.hasPermission(ChallengerPermission.ADMIN) == true }