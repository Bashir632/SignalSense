package com.example.signalsense

import com.example.signalsense.models.SensorItem
import com.example.signalsense.models.SessionSummary
import com.example.signalsense.models.WifiItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class SignalSenseUnitTest {

    @Test
    fun testMathematicalMeanAndVariance() {
        val samples = listOf(10.0, 12.0, 23.0, 23.0, 16.0, 23.0, 21.0, 16.0)
        val mean = samples.average()
        val variance = samples.map { (it - mean).pow(2.0) }.sum() / samples.size
        val stdDev = sqrt(variance)

        assertEquals(18.0, mean, 0.001)
        assertEquals(24.0, variance, 0.001)
        assertEquals(4.8989, stdDev, 0.001)
    }

    @Test
    fun testSignalChangeRateCalculation() {
        val rssiSeries = listOf(-80.0, -82.0, -78.0, -75.0)
        val deltas = rssiSeries.zipWithNext { a, b -> abs(b - a) }.sum()
        val changeRate = deltas / (rssiSeries.size - 1)

        // Deltas: |-82 - (-80)| = 2, |-78 - (-82)| = 4, |-75 - (-78)| = 3 -> sum = 9 / 3 = 3.0
        assertEquals(3.0, changeRate, 0.001)
    }

    @Test
    fun testWifiChannelMapping() {
        fun frequencyToChannel(freq: Int): Int {
            return when {
                freq == 2484 -> 14
                freq in 2412..2472 -> (freq - 2412) / 5 + 1
                freq in 5170..5825 -> (freq - 5170) / 5 + 34
                else -> 0
            }
        }

        assertEquals(1, frequencyToChannel(2412))
        assertEquals(6, frequencyToChannel(2437))
        assertEquals(11, frequencyToChannel(2462))
        assertEquals(14, frequencyToChannel(2484))
        assertEquals(36, frequencyToChannel(5180))
        assertEquals(40, frequencyToChannel(5200))
        assertEquals(0, frequencyToChannel(900))
    }

    @Test
    fun testSensorModel() {
        val item = SensorItem(
            type = 1,
            name = "ICM42688 Accelerometer",
            vendor = "InvenSense",
            unit = "m/s²",
            powerMa = 0.28f,
            isMonitoring = false
        )
        assertEquals("ICM42688 Accelerometer", item.name)
        assertEquals("m/s²", item.unit)
        assertTrue(item.powerMa > 0f)
    }

    @Test
    fun testSessionSummaryDuration() {
        val session = SessionSummary(
            id = 101L,
            name = "Field_Test_Cellular",
            startTime = 1700000000000L,
            endTime = 1700000300000L,
            durationSeconds = 300L,
            totalRecords = 1500L
        )
        assertEquals(5L, session.durationSeconds / 60)
        assertEquals(1500L, session.totalRecords)
    }

    @Test
    fun testSanitizeSensorName() {
        val s1 = com.example.signalsense.utils.SensorUtils.sanitizeSensorName("Goldfish 3-axis Accelerometer")
        val s2 = com.example.signalsense.utils.SensorUtils.sanitizeSensorName("goldfish-gyroscope")
        val s3 = com.example.signalsense.utils.SensorUtils.sanitizeSensorName("GOLDFISH Magnetic Field Sensor")
        val s4 = com.example.signalsense.utils.SensorUtils.sanitizeSensorName("BMA400 Accelerometer")

        assertEquals("3-axis Accelerometer", s1)
        assertEquals("Gyroscope", s2)
        assertEquals("Magnetic Field Sensor", s3)
        assertEquals("BMA400 Accelerometer", s4)
    }
}
