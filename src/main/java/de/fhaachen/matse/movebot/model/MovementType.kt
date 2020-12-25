package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.toBigActivity
import de.fhaachen.matse.movebot.toFinish

enum class MovementType(val title: String, val emoji: String, val unit: String, val pointsPerUnit: Double) {

    BIKING("Radfahren", "\uD83D\uDEB4\u200D♂️", "km", toBigActivity(22.0) ),
    RUNNING("Laufen", "\uD83C\uDFC3\u200D♂️", "km",  toBigActivity(11.0) ),
    WALKING("Spazieren", "\uD83D\uDEB6\u200D♂️", "km", toBigActivity(12.0) ),
    HIKING("Wandern", "⛰", "km", toBigActivity(9.0) ),
    SWIMMING("Schwimmen", "\uD83C\uDFCA\u200D♂️", "km", toBigActivity(2.0) ),
    FITNESS ("Fitness", "\uD83C\uDFCB️\u200D♂️", "min", toBigActivity(60.0) ),
    PUSHUPS ("Liegestütze", "\uD83D\uDE47\uD83C\uDFFC\u200D♂️", "Wdh.", toFinish(15_000)),
    PULLUPS ("Klimmzüge", "\uD83D\uDCAA", "Wdh.", toFinish(7_500)),
    SQUATS ("Kniebeugen", "\uD83E\uDDBF", "Wdh.", toFinish(75_000)),
    PLANKS ("Planks", "\uD83C\uDFF4\u200D☠️", "min", toFinish(4040));

    companion object {
        fun of(input: String) = values().single { it.name.equals(input, true) || it.title.equals(input, true) || it.emoji == input }
    }
}

