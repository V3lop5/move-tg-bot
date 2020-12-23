package de.fhaachen.matse.movebot

val POINT_GOAL = 2021

fun toFinish(value: Int) = POINT_GOAL.toDouble() / value

fun toBigActivity(value: Double) = 15 / value