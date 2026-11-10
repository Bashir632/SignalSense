package com.example.signalsense.models

/**
 * Model representing a Wi-Fi Access Point measurement.
 */
data class WifiItem(
    val ssid: String,
    val bssid: String,
    val rssiDbm: Int,
    val frequencyMhz: Int,
    val channel: Int,
    val timestamp: Long = System.currentTimeMillis()
)
