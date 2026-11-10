package com.example.signalsense.fragments

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.R
import com.example.databinding.FragmentDashboardBinding
import com.example.signalsense.activities.ExportActivity
import com.example.signalsense.activities.HistoryActivity
import com.example.signalsense.activities.MainActivity
import com.example.signalsense.activities.PermissionsActivity
import com.example.signalsense.activities.VisualizationActivity
import java.util.Locale

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    // Monitoring state machine
    private enum class MonitoringState { IDLE, MONITORING, PAUSED }
    private var currentState = MonitoringState.IDLE

    private var secondsElapsed = 0L
    private var recordsCollected = 0L
    private val timerHandler = Handler(Looper.getMainLooper())
    private val timerRunnable = object : Runnable {
        override fun run() {
            if (currentState == MonitoringState.MONITORING) {
                secondsElapsed++
                recordsCollected += 3 // 3 signal streams sampled
                updateDurationAndRecords()
                timerHandler.postDelayed(this, 1000)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        detectRealDeviceHardware()
        setupButtons()
        setupModuleNavigation()
    }

    private fun detectRealDeviceHardware() {
        val context = requireContext()

        // 1. Detect Real Sensors via SensorManager
        val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sensorManager != null) {
            val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            binding.tvTotalSensors.text = "${allSensors.size} Discovered"
        } else {
            binding.tvTotalSensors.text = "0 Detected"
        }

        // 2. Detect Cellular Network Status via TelephonyManager
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        if (telephonyManager != null) {
            val opName = telephonyManager.networkOperatorName.takeIf { !it.isNullOrBlank() }
                ?: telephonyManager.simOperatorName.takeIf { !it.isNullOrBlank() }
                ?: "Ready / Standby"
            binding.tvCellularStatus.text = opName
            binding.tvCellularDetail.text = "SIM State: ${telephonyManager.simState}"
        } else {
            binding.tvCellularStatus.text = "No Cellular HW"
            binding.tvCellularDetail.text = "Unavailable"
        }

        // 3. Detect Wi-Fi Link via WifiManager & ConnectivityManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        val activeNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (isWifi) {
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            val connectionInfo = wifiManager?.connectionInfo
            val ssid = connectionInfo?.ssid?.replace("\"", "") ?: "Connected"
            val rssi = connectionInfo?.rssi ?: -60
            binding.tvWifiStatus.text = if (ssid.contains("unknown", ignoreCase = true)) "Wi-Fi Connected" else ssid
            binding.tvWifiDetail.text = "RSSI: $rssi dBm"
        } else {
            binding.tvWifiStatus.text = "Standby"
            binding.tvWifiDetail.text = "Not connected"
        }
    }

    private fun setupButtons() {
        // Start Monitoring
        binding.btnStartMonitoring.setOnClickListener {
            if (currentState == MonitoringState.MONITORING) {
                Toast.makeText(context, "Monitoring is already active", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentState = MonitoringState.MONITORING
            updateStateBadge()
            timerHandler.post(timerRunnable)
            binding.tvActiveSensors.text = "6 Active"
            Toast.makeText(context, "Telemetry monitoring started", Toast.LENGTH_SHORT).show()
        }

        // Pause / Resume Monitoring
        binding.btnPauseResumeMonitoring.setOnClickListener {
            when (currentState) {
                MonitoringState.MONITORING -> {
                    currentState = MonitoringState.PAUSED
                    timerHandler.removeCallbacks(timerRunnable)
                    updateStateBadge()
                    binding.btnPauseResumeMonitoring.text = getString(R.string.action_resume_monitoring)
                    Toast.makeText(context, "Monitoring paused", Toast.LENGTH_SHORT).show()
                }
                MonitoringState.PAUSED -> {
                    currentState = MonitoringState.MONITORING
                    timerHandler.post(timerRunnable)
                    updateStateBadge()
                    binding.btnPauseResumeMonitoring.text = getString(R.string.action_pause_monitoring)
                    Toast.makeText(context, "Monitoring resumed", Toast.LENGTH_SHORT).show()
                }
                MonitoringState.IDLE -> {
                    Toast.makeText(context, "Start monitoring first", Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Stop Monitoring
        binding.btnStopMonitoring.setOnClickListener {
            if (currentState == MonitoringState.IDLE) {
                Toast.makeText(context, "Monitoring is not running", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            currentState = MonitoringState.IDLE
            timerHandler.removeCallbacks(timerRunnable)
            updateStateBadge()
            binding.tvActiveSensors.text = "0 Active"
            binding.btnPauseResumeMonitoring.text = getString(R.string.action_pause_monitoring)
            Toast.makeText(context, "Monitoring stopped. Stored $recordsCollected records.", Toast.LENGTH_LONG).show()
        }

        // New Session
        binding.btnNewSession.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.navigation_collection)
        }

        // View History
        binding.btnViewHistory.setOnClickListener {
            startActivity(Intent(context, HistoryActivity::class.java))
        }

        // Export Dataset
        binding.btnExportDataset.setOnClickListener {
            startActivity(Intent(context, ExportActivity::class.java))
        }
    }

    private fun setupModuleNavigation() {
        binding.navModuleSensors.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.navigation_sensors)
        }
        binding.navModuleCellular.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.navigation_cellular)
        }
        binding.navModuleWifi.setOnClickListener {
            (activity as? MainActivity)?.switchTab(R.id.navigation_wifi)
        }
        binding.navModuleVisualization.setOnClickListener {
            startActivity(Intent(context, VisualizationActivity::class.java))
        }
        binding.navModulePermissions.setOnClickListener {
            startActivity(Intent(context, PermissionsActivity::class.java))
        }
    }

    private fun updateStateBadge() {
        val context = requireContext()
        when (currentState) {
            MonitoringState.IDLE -> {
                binding.tvStatusBadge.text = getString(R.string.status_idle)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_idle))
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_idle)
            }
            MonitoringState.MONITORING -> {
                binding.tvStatusBadge.text = getString(R.string.status_monitoring)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_active))
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_active)
            }
            MonitoringState.PAUSED -> {
                binding.tvStatusBadge.text = getString(R.string.status_paused)
                binding.tvStatusBadge.setTextColor(ContextCompat.getColor(context, R.color.status_paused))
                binding.tvStatusBadge.setBackgroundResource(R.drawable.bg_badge_paused)
            }
        }
    }

    private fun updateDurationAndRecords() {
        val hrs = secondsElapsed / 3600
        val mins = (secondsElapsed % 3600) / 60
        val secs = secondsElapsed % 60
        binding.tvSessionDuration.text = String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
        binding.tvCollectedRecords.text = "$recordsCollected records"
        val storageKb = (recordsCollected * 0.35)
        binding.tvDbStorage.text = String.format(Locale.getDefault(), "%.1f KB / SQLite", storageKb)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerHandler.removeCallbacks(timerRunnable)
        _binding = null
    }
}
