package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.model.MovementType

val movementTypeParameter = AllowedValuesParameter("Art der Bewegung", "Wähle die Art der Bewegung aus.", MovementType.values().map { it.name })

val distanceParameter = object : Parameter("Distanz", "Gebe die Distanz in Kilometern ein.\n1.2 = 1200 Meter") {
    override fun isValueAllowed(value: String) =
            value.toDoubleOrNull() != null && value.toDouble() < 2000
}