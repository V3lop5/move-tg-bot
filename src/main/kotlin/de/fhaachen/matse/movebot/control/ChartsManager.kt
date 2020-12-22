package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.model.PointStatEntry
import de.fhaachen.matse.movebot.model.SeriesStatEntry
import de.fhaachen.matse.movebot.model.Statistic
import org.knowm.xchart.*
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.style.Styler.LegendPosition
import java.awt.Color
import java.io.File


object ChartsManager {


    fun getChartPicture(statistic: Statistic): File {
        val id = statistic.getUniqueName()
        val chartFile = File(getChartFileName(id))

        if (chartFile.exists())
            return chartFile

        when {
            isSeriesStatistic(statistic) -> {
                val chart: XYChart = generateXYChart(statistic)
                BitmapEncoder.saveBitmap(chart, getChartFileName(id), BitmapEncoder.BitmapFormat.PNG)
            }
            isPieStatistic(statistic) -> {
                val chart: PieChart = generatePieChart(statistic)
                BitmapEncoder.saveBitmap(chart, getChartFileName(id), BitmapEncoder.BitmapFormat.PNG)
            }
            else -> {
                return File("500.png")
            }
        }

        return chartFile
    }

    private fun isSeriesStatistic(statistic: Statistic): Boolean {
        return statistic.statEntries.any { it is SeriesStatEntry }
    }

    private fun isPieStatistic(statistic: Statistic): Boolean {
        return statistic.statEntries.any { it is PointStatEntry }
    }


    private fun getChartFileName(statId: String) = "./chart_${statId.replace(" ", "")}.png"

    private fun generateXYChart(statistic: Statistic): XYChart {
        val chart: XYChart = XYChartBuilder().width(1000).height(750).title(statistic.name).xAxisTitle("X").yAxisTitle("Y").build()

        // Customize Chart
        chart.styler.legendPosition = LegendPosition.InsideNW
        chart.styler.setAxisTitlesVisible(false)
        chart.styler.defaultSeriesRenderStyle = XYSeriesRenderStyle.Line
        chart.styler.chartBackgroundColor = Color.WHITE

        statistic.statEntries.filterIsInstance<SeriesStatEntry>().forEach { entry ->
            chart.addSeries(entry.label, entry.xData, entry.yData)
        }

        return chart
    }


    private fun generatePieChart(statistic: Statistic): PieChart {
        val chart: PieChart = PieChartBuilder().width(1000).height(750).title(statistic.name).build()

        // Customize Chart
        chart.styler.isCircular = true
        chart.styler.chartBackgroundColor = Color.WHITE
        chart.styler.isLegendVisible = true
        chart.styler.legendPosition = LegendPosition.OutsideE

        statistic.statEntries.filterIsInstance<PointStatEntry>().forEach { entry ->
            chart.addSeries(entry.label, entry.value)
        }

        return chart
    }
}