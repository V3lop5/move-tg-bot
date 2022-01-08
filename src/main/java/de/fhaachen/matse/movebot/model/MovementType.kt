package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.byCalories

enum class MovementType(val title: String, val emoji: String, val unit: String, val pointsPerUnit: Double) {

    BIKING("Radfahren", "\uD83D\uDEB4\u200D♂️", "km", byCalories(450, 27) ), // bisschen schwerer gemacht, die Kalorienzahl sind eigentlich für 22km nicht 27km in einer h
    RUNNING("Laufen", "\uD83C\uDFC3\u200D♂️", "km",  byCalories(394, 11) ),
    WALKING("Spazieren", "\uD83D\uDEB6\u200D♂️", "km", byCalories(197, 7) ),
    HIKING("Wandern", "⛰", "km", byCalories(337, 10) ), // Bisschen schwerer gemacht, 10km/h sind ambitioniert
    SWIMMING("Schwimmen", "\uD83C\uDFCA\u200D♂️", "km", byCalories(563, 4) ),
    FITNESS ("Fitness", "\uD83C\uDFCB️\u200D♂️", "min", byCalories(309, 50) ), // Bisschen leichter gemacht, nur 50min in 60min Aktivitätszeit benötigt.
    POWER_FITNESS ("Power Fitness", "\uD83E\uDD3C", "min", byCalories((309 * 1.2).toInt(), 50) ), // Bisschen leichter gemacht, nur 50min in 60min Aktivitätszeit benötigt.
    PUSHUPS ("Liegestütze", "\uD83D\uDE47\uD83C\uDFFC\u200D♂️", "Wdh.", byCalories(450, 216)), // 30 pro Minute, 12% Aktivitätszeit von 1h
    PULLUPS ("Klimmzüge", "\uD83D\uDCAA", "Wdh.", byCalories(450, 115)), // 16 pro Minute, 12% Aktivitätszeit von 1h
    SQUATS ("Kniebeugen", "\uD83E\uDDBF", "Wdh.", byCalories(309, 288)), // 40 pro Minute, 12% Aktivitätszeit von 1h
    PLANKS ("Planks", "\uD83C\uDFF4\u200D☠️", "min", byCalories(337, 20)), // 33% Aktivitätszeit
    SKATING ("Skating", "\uD83D\uDEF9", "min", byCalories(450, 18)); // Biking... Nur langsamer
    //HULAHOOP ("Hula Hoop", "\uD83D\uDD7A", "min", byCalories(337, 75)), // Bisschen schwerer gemacht, 125% Aktivitätszeit
    //BOULDERING ("Bouldern", "\uD83E\uDDD7", "min", byCalories(450, 75)); // Bisschen schwerer gemacht, 125% Aktivitätszeit

    companion object {
        fun of(input: String) = values().single { it.name.equals(input, true) || it.title.equals(input, true) || it.emoji == input }
    }
}

