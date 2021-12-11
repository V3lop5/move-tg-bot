package de.fhaachen.matse.movebot.builder

import de.fhaachen.matse.movebot.model.*
import de.fhaachen.matse.movebot.round
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class StatisticBuilder {

    private val challengers = mutableSetOf<Challenger>()

    private var dateFilterLower: LocalDateTime? = null
    private var dateFilterUpper: LocalDateTime? = null

    private var splitByMovementType: Boolean = false

    private var groupByTime: TimeInterval? = null

    private var cumulateData = false
    private var allowFrayed = false
    private var mentionEveryone = false


    fun addChallenger(challenger: Challenger): StatisticBuilder {
        challengers.add(challenger)
        return this
    }

    fun addChallenger(challengers: Iterable<Challenger>): StatisticBuilder {
        this.challengers.addAll(challengers)
        return this
    }

    fun splitByMovementType(): StatisticBuilder {
        splitByMovementType = true
        return this
    }


    fun onlyDataAfter(date: LocalDate) = onlyDataAfter(date.atStartOfDay())

    fun onlyDataAfter(date: LocalDateTime): StatisticBuilder {
        dateFilterLower = date
        return this
    }

    fun onlyDataBefore(date: LocalDate) = onlyDataBefore(date.atStartOfDay())

    fun onlyDataBefore(date: LocalDateTime): StatisticBuilder {
        dateFilterUpper = date
        return this
    }

    fun onlyDateInInterval(lower: LocalDateTime, upper: LocalDateTime): StatisticBuilder {
        dateFilterLower = lower
        dateFilterUpper = upper
        return this
    }


    fun onlyDateInInterval(date: LocalDateTime, range: Long, type: ChronoUnit): StatisticBuilder {
        if (range < 0) {
            dateFilterUpper = date
            dateFilterLower = date.plus(range, type)
        } else {
            dateFilterLower = date
            dateFilterUpper = date.plus(range, type)
        }

        return this
    }

    fun onlyDateInInterval(date: LocalDate, range: Long, type: ChronoUnit): StatisticBuilder {
        if (range < 0) {
            dateFilterUpper = date.atTime(23, 59)
            dateFilterLower = date.plus(range, type).atStartOfDay()
        } else {
            dateFilterLower = date.atStartOfDay()
            dateFilterUpper = date.plus(range, type).atTime(23, 59)
        }

        return this
    }

    fun cumulateData(): StatisticBuilder {
        cumulateData = true
        return this
    }

    fun groupByTimeInterval(timeInterval: TimeInterval): StatisticBuilder {
        groupByTime = timeInterval
        return this
    }


    fun allowFrayed(): StatisticBuilder {
        allowFrayed = true
        return this
    }

    fun build(name: String): Statistic {
        val entries = mutableListOf<StatEntry>()


        challengers.forEach { challenger ->
            val statData = challenger.movements
                    .filter { dateFilterLower == null || it.datetime.isAfter(dateFilterLower) }
                    .filter { dateFilterUpper == null || it.datetime.isBefore(dateFilterUpper) }
                    .groupBy {
                        challenger.nickname + if (splitByMovementType) " " + it.type.name else ""
                    }

            // Jetzt die einzelnen Movements zu StatEntries wandeln
            statData.forEach { data ->
                if (groupByTime == null) {
                    entries.add(PointStatEntry(data.key, challenger, data.value.sumByDouble { it.points }.round(2)))
                } else {
                    val intervalData = data.value.groupBy { groupByTime!!.groupBy(it) }
                    val xData = IntRange(intervalData.minByOrNull { it.key }?.key ?: 0, intervalData.maxByOrNull { it.key }?.key
                            ?: 0)
                    val yData = xData.map { x ->
                        if (cumulateData) intervalData.filterKeys { it <= x }.values.sumByDouble { movement -> movement.sumByDouble { it.points } }
                        else intervalData[x]?.sumByDouble { it.points } ?: 0.0
                    }.map { it.round(2) }
                    entries.add(SeriesStatEntry(data.key, challenger, xData.map { it.toDouble() }.toMutableList(), yData.toMutableList(), cumulateData))
                }
            }
        }

        if (!allowFrayed && groupByTime != null) {
            val minX = entries.filterIsInstance<SeriesStatEntry>().map { it.xData.minOrNull() ?: 0.0 }.minOrNull() ?: 0.0
            val maxX = entries.filterIsInstance<SeriesStatEntry>().map { it.xData.maxOrNull() ?: 1.0 }.maxOrNull() ?: 1.0

            entries.filterIsInstance<SeriesStatEntry>().forEach { entry ->
                if (entry.xData.minOrNull() ?: 0.0 > minX) {
                    entry.xData.add(0, minX)
                    entry.yData.add(0, 0.0)
                }

                if (entry.xData.maxOrNull() ?: 0.0 < maxX) {
                    entry.xData.add(maxX)
                    entry.yData.add(entry.yData.lastOrNull() ?: 0.0)
                }
            }
        }

        return Statistic(name, entries)
    }

}

