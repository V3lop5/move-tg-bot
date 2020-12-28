package de.fhaachen.matse.movebot.telegram.model

import de.fhaachen.matse.movebot.model.MovementType

val movementTypeParameter = AllowedValuesParameter("Art der Bewegung", "Wähle die Art der Bewegung aus.", MovementType.values().map { it.name })

val movementValueParameter = object : Parameter("Distanz", "Gebe die Distanz in Kilometern ein.\n1.2 = 1200 Meter") {

    val unitDescriptions = mapOf("km" to "Distanz in Kilometern", "min" to "Aktivitätsdauer in Minuten", "Wdh." to "Anzahl der Wiederholungen")
    val unitExamples = mapOf("km" to "`5.6` für 5.6 km / 5600 Meter", "min" to "`30` für eine halbe Stunde", "Wdh." to "`20` für 20")

    override fun isValueAllowed(value: String) =
            value.toDoubleOrNull() != null && value.toDouble() < 2000 && value.toDouble() > 0

    override fun getHelptext(parsedValues: List<String>): String {
        val movementType = parsedValues.lastOrNull()?.let { MovementType.of(it) }

        return if (movementType == null) "Aktivitäten werden entweder in Kilometer, Minuten oder Wiederholungen gemessen.\n" +
                "Bitte gebe die Distanz in Kilometern, die Aktivitätsdauer in Minuten oder die Anzahl der Wiederholungen ein.\n" +
                "Falls nötig kannst du auch mit Kommazahlen arbeiten. Beispiel: `1.2` [km] entsprechen 1200 Metern"
        else "Bitte gebe für die Aktivität *${movementType.name}* die _${unitDescriptions[movementType.unit]}_ an.\n\nBeispiel: ${unitExamples[movementType.unit]} ${movementType.title}"
    }
}

val goalParameter = object : Parameter("Ziel", "Gebe das Ziel für die Sporart ein.") {

    val unitDescriptions = mapOf("km" to "Distanz in Kilometern", "min" to "Aktivitätsdauer in Minuten", "Wdh." to "Anzahl der Wiederholungen")
    val unitExamples = mapOf("km" to "`2000` für 2000 Kilometer", "min" to "`600` für 10 Stunden", "Wdh." to "`6666` für 6666")

    override fun isValueAllowed(value: String) =
        value.toDoubleOrNull() != null && value.toDouble() > 0

    override fun getHelptext(parsedValues: List<String>): String {
        val movementType = parsedValues.lastOrNull()?.let { MovementType.of(it) }

        return if (movementType == null) "Gebe dein Ziel bis zum Ende des Jahres ein. Je nach Sportart entweder in Kilometern, Minuten oder Wiederholungen."
        else "Gebe für die Sportart *${movementType.name}* dein Jahresziel als _${unitDescriptions[movementType.unit]}_ an.\n\nBeispiel: ${unitExamples[movementType.unit]} ${movementType.title}\n\nWas ist dein Ziel bis zum Ende des Jahres?"
    }
}