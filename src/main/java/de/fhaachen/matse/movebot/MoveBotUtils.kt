package de.fhaachen.matse.movebot

const val POINT_GOAL = 2021

fun toFinish(value: Int) = POINT_GOAL.toDouble() / value

/**
 * 50_500 Kalorien zum Beenden der Challenge.
 *
 * Berechnung:
 * Es bedarf 50_500 Kalorien zum Beenden der Challenge. Also müssen pro Punkt toFinish(50_500) Kalorien erarbeitet werden.
 * calPerHour / unitsPerHour = Kalorie pro Einheit
 * Kalorie pro Einheit / Kalorien pro Punkt = Punkt pro Einheit
 *
 * @param calPerHour Int Kalorien pro Stunde
 * @param unitsPerHour Int Einheiten pro Stunde
 * @return Double Punkte für eine Einheit
 */
fun byCalories(calPerHour: Int, unitsPerHour: Int) = (calPerHour.toDouble() / unitsPerHour) / (50_500.0 / POINT_GOAL)