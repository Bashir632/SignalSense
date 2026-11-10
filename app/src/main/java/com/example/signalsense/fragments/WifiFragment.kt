package com.example.signalsense.fragments

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databinding.FragmentWifiBinding
import com.example.signalsense.adapters.WifiAdapter
import com.example.signalsense.models.WifiItem

class WifiFragment : Fragment() {

    private var _binding: FragmentWifiBinding? = null
    private val binding get() = _binding!!

    private lateinit var wifiAdapter: WifiAdapter
    private val wifiAccessPoints = mutableListOf<WifiItem>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWifiBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        inspectWifiTelemetry()
    }

    private fun setupRecyclerView() {
        wifiAdapter = WifiAdapter(wifiAccessPoints)
        binding.rvWifiList.layoutManager = LinearLayoutManager(context)
        binding.rvWifiList.adapter = wifiAdapter
    }

    private fun inspectWifiTelemetry() {
        val context = requireContext()
        val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

        val activeNetwork = connectivityManager?.activeNetwork
        val caps = connectivityManager?.getNetworkCapabilities(activeNetwork)
        val isWifi = caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true

        if (wifiManager != null && isWifi) {
            val info = wifiManager.connectionInfo
            val rawSsid = info.ssid ?: "Unknown"
            val cleanSsid = rawSsid.replace("\"", "")
            val bssid = info.bssid ?: "Unavailable"
            val rssi = info.rssi
            val freq = info.frequency
            val channel = frequencyToChannel(freq)

            binding.tvConnectedSsid.text = if (cleanSsid.contains("unknown", ignoreCase = true)) "Wi-Fi Network" else cleanSsid
            binding.tvWifiRssiBadge.text = "$rssi dBm"
            binding.tvConnectedBssid.text = "BSSID: $bssid"
            binding.tvConnectedFreq.text = "Frequency: $freq MHz | Channel $channel | ${if (freq > 4900) "5 GHz" else "2.4 GHz"}"

            // Add connected AP to top of list
            wifiAccessPoints.clear()
            wifiAccessPoints.add(
                WifiItem(
                    ssid = cleanSsid,
                    bssid = bssid,
                    rssiDbm = rssi,
                    frequencyMhz = freq,
                    channel = channel
                )
            )
            wifiAdapter.updateList(wifiAccessPoints)
        } else {
            binding.tvConnectedSsid.text = "Wi-Fi Disconnected / Standby"
            binding.tvWifiRssiBadge.text = "OFFLINE"
            binding.tvConnectedBssid.text = "BSSID: --:--:--:--:--:--"
            binding.tvConnectedFreq.text = "Enable Wi-Fi or connect to a local access point"
        }
    }

    private fun frequencyToChannel(freq: Int): Int {
        return when {
            freq == 2484 -> 14
            freq in 2412..2472 -> (freq - 2412) / 5 + 1
            freq in 5170..5825 -> (freq - 5170) / 5 + 34
            else -> 0
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
