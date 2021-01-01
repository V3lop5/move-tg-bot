package de.fhaachen.matse.movebot.control

import de.fhaachen.matse.movebot.model.PointStatEntry
import de.fhaachen.matse.movebot.model.SeriesStatEntry
import de.fhaachen.matse.movebot.model.Statistic
import org.knowm.xchart.*
import org.knowm.xchart.XYSeries.XYSeriesRenderStyle
import org.knowm.xchart.internal.chartpart.Chart
import org.knowm.xchart.style.PieStyler
import org.knowm.xchart.style.Styler.LegendPosition
import java.awt.Color
import java.io.File


object ChartsManager {

    fun getChartPicture(statistic: Statistic, asStackedBar: Boolean = false): File {
        val id = statistic.getUniqueName()
        val chartFile = File(getChartFileName(id))

        if (chartFile.exists())
            return chartFile


        val chart = when {
            asStackedBar -> generateStackedChart(statistic)

            isSeriesStatistic(statistic) -> generateXYChart(statistic)

            isPieStatistic(statistic) -> generatePieChart(statistic)

            else -> {
                return File("500.png")
            }
        }

        BitmapEncoder.saveBitmapWithDPI(chart, getChartFileName(id), BitmapEncoder.BitmapFormat.PNG, 300)

        return chartFile
    }

    private fun isSeriesStatistic(statistic: Statistic): Boolean {
        return statistic.statEntries.any { it is SeriesStatEntry }
    }

    private fun isPieStatistic(statistic: Statistic): Boolean {
        return statistic.statEntries.any { it is PointStatEntry }
    }


    private fun getChartFileName(statId: String) = "./charts/chart_${statId.replace(" ", "")}.png"

    private fun generateXYChart(statistic: Statistic): XYChart {
        val chart: XYChart =
            XYChartBuilder().width(1300).height(975).title(statistic.name).xAxisTitle("X").yAxisTitle("Y").build()

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
        chart.styler.annotationType = PieStyler.AnnotationType.Percentage
        chart.styler.annotationDistance = 0.82
        chart.styler.defaultSeriesRenderStyle = PieSeries.PieSeriesRenderStyle.Donut
        chart.styler.seriesColors =
            arrayOf(Color(1, 74, 127), Color(253, 216, 11), Color(128, 0, 1), Color(165, 254, 4))

        statistic.statEntries.filterIsInstance<PointStatEntry>().forEach { entry ->
            chart.addSeries(entry.label + " (${entry.value} km)", entry.value)
        }

        return chart
    }

    private fun generateStackedChart(statistic: Statistic): CategoryChart {
        val chart: CategoryChart = CategoryChartBuilder().width(1000).height(750).title(statistic.name).build()

        // Customize Chart
        chart.styler.chartBackgroundColor = Color.WHITE
        chart.styler.isLegendVisible = true
        chart.styler.legendPosition = LegendPosition.InsideNW
        chart.styler.isOverlapped = true
        chart.styler.seriesColors =
            arrayOf(Color(1, 74, 127), Color(253, 216, 11), Color(128, 0, 1), Color(165, 254, 4))

        statistic.statEntries.filterIsInstance<SeriesStatEntry>().forEach { entry ->
            chart.addSeries(entry.label, entry.xData, entry.yData)
        }

        return chart
    }
}