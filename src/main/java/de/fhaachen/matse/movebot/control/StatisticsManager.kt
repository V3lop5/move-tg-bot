package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.builder.StatisticBuilder
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.model.Statistic
import de.fhaachen.matse.movebot.model.TimeInterval
import de.fhaachen.matse.movebot.round
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

object StatisticsManager {

    fun getPoints(challenger: Challenger, movementType: MovementType? = null): Double {
        val points = (if (movementType != null)
            challenger.movements.filter { it.type == movementType }
        else challenger.movements)
                .map { it.points }.sum()

        return points.round(2)
    }


    fun getSum(challenger: Challenger, movementType: MovementType? = null, after: LocalDateTime? = null): Double {
        return challenger.movements
            .filter { movementType == null || movementType == it.type }
            .filter { after == null || it.datetime.isAfter(after) }
            .map { it.value }.sum().round(2)
    }

    fun getCompetitorStatistic(timeInterval: TimeInterval = TimeInterval.WEEKLY) = StatisticBuilder()
            .addChallenger(ChallengerManager.challengers)
            .groupByTimeInterval(timeInterval)
            .cumulateData()
            .build("Competitors (${timeInterval.name})")

    /**
     * Weekly Statistic from 18 o'clock of current day 7 days back
     * @return Statistic
     */
    fun getLastWeekStatistic() = StatisticBuilder()
            .addChallenger(ChallengerManager.challengers)
            .onlyDateInInterval(LocalDate.now().atTime(19, 50), -7, ChronoUnit.DAYS)
            .groupByTimeInterval(TimeInterval.DAILY)
            .cumulateData()
            .build("Competitors (last week)")

    fun getMovementSplittedByType(challenger: Challenger) = StatisticBuilder().addChallenger(challenger).splitByMovementType().build("${challenger.nickname}'s Aktivitäten")

    fun getCurrentMonthPoints(challenger: Challenger) = challenger.movements.filter { it.datetime.month == LocalDate.now().month }.sumByDouble { it.points }.round(2)

    fun getMonthlyStatistic(): Statistic = StatisticBuilder()
            .addChallenger(ChallengerManager.challengers)
            .groupByTimeInterval(TimeInterval.MONTHLY)
            .build("Competitors (${TimeInterval.MONTHLY.name}, uncollated)")
}