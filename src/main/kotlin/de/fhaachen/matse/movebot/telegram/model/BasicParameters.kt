package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.model.MovementType

val movementTypeParameter = AllowedValuesParameter("Art der Bewegung", "Wähle die Art der Bewegung aus.", MovementType.values().map { it.name })

val movementValueParameter = object : Parameter("Distanz", "Gebe die Distanz in Kilometern ein.\n1.2 = 1200 Meter") {
    override fun isValueAllowed(value: String) =
            value.toDoubleOrNull() != null && value.toDouble() < 2000
}

val goalParameter = object : Parameter("Ziel", "Gebe das Ziel für die Sporart ein. Beispiele:\n`520` = 520 km Running\n`1000` = 1000 Wiederholungen Liegestütze") {
    override fun isValueAllowed(value: String) =
        value.toIntOrNull() != null
}