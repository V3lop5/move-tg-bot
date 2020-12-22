package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.builder.StatisticBuilder
import de.fhaachen.matse.movebot.model.Challenger
import de.fhaachen.matse.movebot.model.MovementType
import de.fhaachen.matse.movebot.model.Statistic
import de.fhaachen.matse.movebot.model.TimeInterval
import de.fhaachen.matse.movebot.round
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object StatisticsManager {

    fun getDistance(challenger: Challenger, movementType: MovementType? = null): Double {
        val distance = (if (movementType != null)
            challenger.movements.filter { it.type == movementType }
        else challenger.movements)
                .map { it.distance }.sum()

        return distance.round(2)
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
            .onlyDateInInterval(LocalDate.now().atTime(18, 0), -7, ChronoUnit.DAYS)
            .groupByTimeInterval(TimeInterval.DAILY)
            .cumulateData()
            .build("Competitors (last week)")

    fun getMovementSplittedByType(challenger: Challenger) = StatisticBuilder().addChallenger(challenger).splitByMovementType().build("${challenger.nickname}'s Aktivitäten")

    fun getCurrentMonthDistance(challenger: Challenger) = challenger.movements.filter { it.datetime.month == LocalDate.now().month }.sumByDouble { it.distance }.round(2)

    fun getMonthlyStatistic(): Statistic = StatisticBuilder()
            .addChallenger(ChallengerManager.challengers)
            .groupByTimeInterval(TimeInterval.MONTHLY)
            .build("Competitors (${TimeInterval.MONTHLY.name}, uncollated)")
}