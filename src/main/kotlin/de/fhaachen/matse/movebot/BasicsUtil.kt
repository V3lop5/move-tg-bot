package de.fhaachen.matse.movebot

import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.pow

private val dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")
private val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
private val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

fun Double.round(n: Int): Double {
    val factor = (10.0).pow(n)
    return kotlin.math.round(this * factor) / factor
}

fun LocalDateTime.prettyString() = this.format(dateTimeFormatter)!!

fun LocalDateTime.prettyDateString() = this.format(dateFormatter)!!
fun LocalTime.prettyString() = this.format(timeFormatter)!!


fun Number.length() = when (this) {
    0 -> 1
    else -> log10(abs(toDouble())).toInt() + 1
}

fun Number.padByMaxValue(max: Int) = padStart(max.length())

fun Number.padStart(length: Int) = this.toString().padStart(length)
