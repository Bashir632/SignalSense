package com.example.signalsense.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databinding.ActivityHistoryBinding
import com.example.signalsense.adapters.HistoryAdapter
import com.example.signalsense.models.SessionSummary

class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var historyAdapter: HistoryAdapter
    private val sessionsList = mutableListOf<SessionSummary>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarHistory.setNavigationOnClickListener {
            finish()
        }

        setupRecyclerView()
        loadSampleSessions()
    }

    private fun setupRecyclerView() {
        historyAdapter = HistoryAdapter(sessionsList) { session ->
            val intent = Intent(this, ExportActivity::class.java).apply {
                putExtra("EXTRA_SESSION_NAME", session.name)
            }
            startActivity(intent)
        }
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = historyAdapter
    }

    private fun loadSampleSessions() {
        sessionsList.clear()
        // Provide baseline completed session records
        val now = System.currentTimeMillis()
        sessionsList.add(
            SessionSummary(
                id = 1L,
                name = "Baseline_Sensor_Run_01",
                startTime = now - 7200000,
                endTime = now - 7020000,
                durationSeconds = 180,
                totalRecords = 108
            )
        )
        sessionsList.add(
            SessionSummary(
                id = 2L,
                name = "Indoor_Wifi_Cellular_Sweep",
                startTime = now - 3600000,
                endTime = now - 3360000,
                durationSeconds = 240,
                totalRecords = 144
            )
        )

        if (sessionsList.isEmpty()) {
            binding.layoutEmptyHistory.visibility = View.VISIBLE
            binding.rvHistory.visibility = View.GONE
        } else {
            binding.layoutEmptyHistory.visibility = View.GONE
            binding.rvHistory.visibility = View.VISIBLE
            historyAdapter.updateList(sessionsList)
        }
    }
}
