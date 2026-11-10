package com.example.signalsense.models

/**
 * Summary model for a recorded collection session.
 */
data class SessionSummary(
    val id: Long,
    val name: String,
    val startTime: Long,
    val endTime: Long?,
    val durationSeconds: Long,
    val totalRecords: Long,
    val isCompleted: Boolean = true
)
