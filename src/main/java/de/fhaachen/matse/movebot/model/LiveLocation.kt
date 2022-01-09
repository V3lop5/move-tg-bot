package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.round
import org.telegram.telegrambots.meta.api.objects.Location
import org.telegram.telegrambots.meta.api.objects.Message
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

class LiveLocation(val infoMessage: Message) {

    val locations = mutableListOf<Location>()
    var distance = 0.0

    fun addLocation(location: Location) {
        if (locations.isNotEmpty())
            distance += calcDistance(locations.last(), location)
        locations.add(location)
    }

    private fun calcDistance(from: Location, to: Location): Double {
        return calcDistance(from.latitude, from.longitude, to.latitude, to.longitude).round(3)
    }

    private fun calcDistance(latA: Double, longA: Double, latB: Double, longB: Double): Double {
        val theDistance =
                sin(Math.toRadians(latA)) *
                        sin(Math.toRadians(latB)) +
                        cos(Math.toRadians(latA)) *
                        cos(Math.toRadians(latB)) *
                        cos(Math.toRadians(longA - longB))

        return (Math.toDegrees(acos(theDistance))) * 69.09 * 1.6093
    }

}