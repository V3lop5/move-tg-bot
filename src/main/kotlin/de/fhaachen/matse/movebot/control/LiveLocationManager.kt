package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.model.LiveLocation
import de.fhaachen.matse.movebot.telegram.ChallengeBot
import de.fhaachen.matse.movebot.telegram.MessageHandler
import de.fhaachen.matse.movebot.telegram.model.MessageCleanupCause
import de.fhaachen.matse.movebot.telegram.model.MessageType
import de.fhaachen.matse.movebot.telegram.model.inlineKeyboardFromPair
import org.telegram.telegrambots.meta.api.objects.Message

object LiveLocationManager {

    private val END_KEYBOARD = inlineKeyboardFromPair(Pair("Aufzeichnung beenden", "#endlive"))

    val liveLocations = mutableMapOf<Long, LiveLocation>()

    fun hasLiveLocation(chatId: Long) = liveLocations.containsKey(chatId)

    fun startLiveLocation(chatId: Long) {
        val infoMessage = ChallengeBot.sendMessage(chatId, "Die Aufzeichnung deiner Aktivität beginnt!", END_KEYBOARD)
        MessageHandler.addDeleteableMessage(infoMessage, MessageType.LIVE_LOCATION)
        liveLocations[chatId] = LiveLocation(infoMessage)

        MessageHandler.cleanupMessages(chatId, MessageCleanupCause.LIVE_LOCATION_START)
    }

    fun endLiveLocation(chatId: Long): Double {
        val liveloc = liveLocations.remove(chatId) ?: return 0.0

        ChallengeBot.sendMessage(chatId, "Das Tracking mithilfe deines Livestandorts wurde beendet!\n" +
                "Distanz: *${liveloc.distance} Kilometer*\n" +
                "Aktualisierungen: *${liveloc.locations.size} Standorte*")

        MessageHandler.cleanupMessages(chatId, MessageCleanupCause.LIVE_LOCATION_END)

        return liveloc.distance
    }

    fun onLiveLocationUpdate(message: Message) {
        if (!hasLiveLocation(message.chatId)) {
            startLiveLocation(message.chatId)
        }

        val liveLoc = liveLocations[message.chatId] ?: return
        liveLoc.addLocation(message.location)

        if (liveLoc.distance > 0)
            ChallengeBot.sendEditText(liveLoc.infoMessage, "*Aktivität wird aufgezeichnet!*\n" +
                    "Du hast *${liveLoc.distance} Kilometer* zurückgelegt.\n" +
                    "(${liveLoc.locations.size} Standortaktualisierungen)", END_KEYBOARD)

        if (!MessageHandler.isDeleteableMessage(message))
            MessageHandler.addDeleteableMessage(message, MessageType.LIVE_LOCATION)


    }

    fun onLocationMessage(message: Message) {
        ChallengeBot.sendMessage(message.chatId, "Ich habe deinen Standort erhalten.\n" +
                "Wenn dies ein Live-Standort ist, wird deine Aktivität aufgezeichnet.\n" +
                "Das Tracking beginnt, sobald du dich bewegst... Ich bin bereit!")
                .also { MessageHandler.addDeleteableMessage(it, MessageType.LOCATION_RECIVED) }
    }
}