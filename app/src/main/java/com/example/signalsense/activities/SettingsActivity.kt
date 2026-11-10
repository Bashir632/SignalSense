package com.example.signalsense.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import com.example.databinding.ActivitySettingsBinding
import com.example.signalsense.utils.PreferencesHelper

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: PreferencesHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferencesHelper(this)

        binding.toolbarSettings.setNavigationOnClickListener {
            finish()
        }

        loadSettings()

        binding.rgSensorDelay.setOnCheckedChangeListener { _, checkedId ->
            val option = when (checkedId) {
                R.id.rbDelayFastest -> 2
                R.id.rbDelayUi -> 1
                else -> 0
            }
            prefs.samplingDelayOption = option
            Toast.makeText(this, "Sampling rate updated", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadSettings() {
        when (prefs.samplingDelayOption) {
            2 -> binding.rbDelayFastest.isChecked = true
            1 -> binding.rbDelayUi.isChecked = true
            else -> binding.rbDelayNormal.isChecked = true
        }

        binding.etBackendIp.setText(prefs.backendIp)
        binding.etBackendPort.setText(prefs.backendPort)
    }

    override fun onPause() {
        super.onPause()
        prefs.backendIp = binding.etBackendIp.text?.toString() ?: "192.168.1.100"
        prefs.backendPort = binding.etBackendPort.text?.toString() ?: "8000"
    }
}
