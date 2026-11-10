package com.example.signalsense.activities

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.ActivityVisualizationBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import java.util.Locale
import kotlin.math.pow
import kotlin.math.sqrt

class VisualizationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVisualizationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVisualizationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarVisualization.setNavigationOnClickListener {
            finish()
        }

        setupChart()
        loadDataForStream("Accelerometer")

        binding.chipGroupSignals.setOnCheckedStateChangeListener { _, checkedIds ->
            when {
                checkedIds.contains(R.id.chipAccel) -> loadDataForStream("Accelerometer")
                checkedIds.contains(R.id.chipGyro) -> loadDataForStream("Gyroscope")
                checkedIds.contains(R.id.chipMag) -> loadDataForStream("Magnetometer")
                checkedIds.contains(R.id.chipCellular) -> loadDataForStream("Cellular RSRP")
                checkedIds.contains(R.id.chipWifi) -> loadDataForStream("Wi-Fi RSSI")
            }
        }
    }

    private fun setupChart() {
        val chart = binding.lineChart
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        chart.setDrawGridBackground(false)

        val xAxis = chart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.textColor = ContextCompat.getColor(this, R.color.on_surface_variant)
        xAxis.setDrawGridLines(false)

        val leftAxis = chart.axisLeft
        leftAxis.textColor = ContextCompat.getColor(this, R.color.on_surface_variant)
        leftAxis.setDrawGridLines(true)

        val rightAxis = chart.axisRight
        rightAxis.isEnabled = false

        chart.legend.textColor = ContextCompat.getColor(this, R.color.on_surface)
    }

    private fun loadDataForStream(streamName: String) {
        binding.tvChartTitle.text = "$streamName Telemetry Waveform"

        val values = when (streamName) {
            "Accelerometer" -> listOf(9.81, 9.79, 9.83, 9.80, 9.85, 9.78, 9.82, 9.81, 9.84, 9.80)
            "Gyroscope" -> listOf(0.02, -0.01, 0.04, -0.02, 0.03, 0.01, -0.03, 0.02, -0.01, 0.00)
            "Magnetometer" -> listOf(45.2, 45.4, 45.1, 45.5, 45.3, 45.6, 45.2, 45.1, 45.4, 45.3)
            "Cellular RSRP" -> listOf(-98.0, -97.0, -99.0, -98.0, -96.0, -97.0, -99.0, -98.0, -97.0, -98.0)
            else -> listOf(-54.0, -56.0, -53.0, -55.0, -52.0, -54.0, -55.0, -53.0, -56.0, -54.0)
        }

        val entries = values.mapIndexed { index, value ->
            Entry(index.toFloat(), value.toFloat())
        }

        val primaryColor = ContextCompat.getColor(this, R.color.primary)
        val dataSet = LineDataSet(entries, streamName).apply {
            color = primaryColor
            setCircleColor(primaryColor)
            lineWidth = 2.5f
            circleRadius = 4f
            setDrawCircleHole(false)
            valueTextSize = 9f
            valueTextColor = ContextCompat.getColor(this@VisualizationActivity, R.color.on_surface)
            mode = LineDataSet.Mode.CUBIC_BEZIER
        }

        binding.lineChart.data = LineData(dataSet)
        binding.lineChart.invalidate()

        calculateStatistics(values)
    }

    private fun calculateStatistics(values: List<Double>) {
        if (values.isEmpty()) return

        val count = values.size
        val mean = values.average()
        val min = values.minOrNull() ?: 0.0
        val max = values.maxOrNull() ?: 0.0

        // Standard Deviation
        val variance = values.map { (it - mean).pow(2.0) }.sum() / count
        val stdDev = sqrt(variance)

        // Signal Change Rate (Average absolute delta between successive samples)
        val deltaSum = values.zipWithNext { a, b -> kotlin.math.abs(b - a) }.sum()
        val changeRate = if (count > 1) deltaSum / (count - 1) else 0.0

        binding.tvStatMean.text = String.format(Locale.getDefault(), "%.3f", mean)
        binding.tvStatStdDev.text = String.format(Locale.getDefault(), "%.3f (var: %.3f)", stdDev, variance)
        binding.tvStatMinMax.text = String.format(Locale.getDefault(), "%.2f / %.2f", min, max)
        binding.tvStatChangeRate.text = String.format(Locale.getDefault(), "%.3f /sec", changeRate)
    }
}
