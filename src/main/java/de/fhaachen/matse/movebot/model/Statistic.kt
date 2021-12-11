package de.fhaachen.matse.movebot.model

import de.fhaachen.matse.movebot.round

data class Statistic(
        val name: String,
        val statEntries: MutableList<StatEntry>
) {

    fun getUniqueName(): String {
        return name + "_" + statEntries.toTypedArray().contentHashCode()
    }

    fun containsChallenger(challenger: Challenger) = statEntries.any { it.challenger == challenger }

    fun sortByValue(ascending: Boolean = false) = if (ascending) statEntries.sortBy { it.value() } else statEntries.sortByDescending { it.value() }
    fun sortByLabel(ascending: Boolean = false) = if (ascending) statEntries.sortBy { it.label } else statEntries.sortByDescending { it.label }
}

abstract class StatEntry(
        val label: String,
        val challenger: Challenger
) {
    abstract fun value(): Double


    override fun hashCode(): Int {
        return (label.hashCode() + 47 * value()).toInt()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is StatEntry) return false

        if (label != other.label || value() != other.value()) return false

        return true
    }


}

class PointStatEntry(
        label: String,
        challenger: Challenger,
        val value: Double
) : StatEntry(label, challenger) {
    override fun value(): Double {
        return value
    }

}

class SeriesStatEntry(
        label: String,
        challenger: Challenger,
        val xData: MutableList<Double>,
        val yData: MutableList<Double>,
        private val cumulatedData: Boolean
) : StatEntry(label, challenger) {
    override fun value(): Double {
        return if (cumulatedData) yData.maxOrNull() ?: 0.0 else yData.sum().round(2)
    }


}