package com.example.signalsense.utils

object SensorUtils {

    /**
     * Sanitizes sensor names by removing emulator-specific prefixes like 'Goldfish'
     * from the beginning of the sensor name while preserving proper title casing.
     * E.g. "Goldfish 3-axis Accelerometer" -> "3-axis Accelerometer"
     *      "goldfish-gyroscope" -> "Gyroscope"
     */
    fun sanitizeSensorName(rawName: String): String {
        val sanitized = rawName.replace(Regex("(?i)^goldfish\\s*[-_]?\\s*"), "").trim()
        return if (sanitized.isNotEmpty()) {
            sanitized.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        } else {
            rawName
        }
    }
}
