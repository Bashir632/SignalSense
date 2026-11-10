package com.example.signalsense.models

/**
 * Model representing a discovered hardware sensor on the smartphone.
 */
data class SensorItem(
    val type: Int,
    val name: String,
    val vendor: String,
    val unit: String,
    val powerMa: Float,
    var isMonitoring: Boolean = false,
    var currentValues: String = "X: 0.000  Y: 0.000  Z: 0.000"
)
