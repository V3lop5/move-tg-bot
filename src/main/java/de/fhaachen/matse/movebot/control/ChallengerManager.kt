package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.handler.ChallengerHandler
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.ChallengerPermission
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.getName
import org.telegram.telegrambots.meta.api.objects.User
import java.time.LocalDateTime

object ChallengerManager {

    val challengers = loadChallengers()

    fun isChallenger(telegramUser: User) = challengers.any { it.telegramUser.id == telegramUser.id }

    fun addChallenger(telegramUser: User) {
        if (isChallenger(telegramUser)) return

        val challenger = Challenger(telegramUser)
        challengers.add(challenger)

        ChallengerHandler.onChallengerAdd(challenger)
    }

    fun removeChallenger(telegramUser: User) {
        if (!isChallenger(telegramUser))
            return

        challengers.removeIf { it.telegramUser.id == telegramUser.id }
    }

    fun findChallenger(telegramUser: User) = findChallenger(telegramUser.id)

    fun findChallenger(id: Int) = challengers.firstOrNull { it.telegramUser.id == id }

    fun findChallenger(name: String) = challengers.firstOrNull {
        name.equals(it.customNickname, true) ||
                name.equals(it.telegramUser.userName, true) ||
                name.equals(it.telegramUser.getName(), true)
    }

    fun save() {
        saveChallengers(challengers)
    }

    fun countChallengers(onlyActive: Boolean = false): Int {
        return if (onlyActive)
            challengers.count {
                it.movements.any { movement ->
                    movement.datetime.isAfter(
                        LocalDateTime.now().minusDays(7)
                    )
                }
            }
        else challengers.size
    }


    fun sendError(cause: String, e: Exception? = null) {
        val message =
            "!!! Fehler !!!\n*$cause*" + if (e != null) "\n\nMessage: ${e.message}\nTrace: ${e.stackTrace.joinToString("\n")}" else "\n\nKeine weiteren Informationen verfügbar."
        challengers.filter { it.hasPermission(ChallengerPermission.ADMIN) }.forEach { challenger ->
            try {
                ChallengeBot.sendMessage(challenger.telegramUser.id, message)
            } catch (e: Exception) {
            }
        }
        System.err.println(message)
    }


}