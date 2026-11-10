package com.example.signalsense.activities

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.databinding.ActivityDeviceInfoBinding

class DeviceInfoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDeviceInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarDeviceInfo.setNavigationOnClickListener {
            finish()
        }

        populateDeviceInfo()
        populateHardwareSensors()
    }

    private fun populateDeviceInfo() {
        binding.tvDeviceModel.text = Build.MODEL
        binding.tvDeviceManufacturer.text = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }
        binding.tvDeviceAndroidVersion.text = "Android ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"
        binding.tvDeviceFingerprint.text = Build.FINGERPRINT
    }

    private fun populateHardwareSensors() {
        val sm = getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        if (sm == null) {
            binding.tvSensorListOverview.text = "SensorManager unavailable on this device."
            return
        }

        val allSensors = sm.getSensorList(Sensor.TYPE_ALL)
        val sb = StringBuilder()
        sb.append("Total Hardware Sensors Detected: ${allSensors.size}\n\n")

        allSensors.forEachIndexed { index, sensor ->
            val cleanName = com.example.signalsense.utils.SensorUtils.sanitizeSensorName(sensor.name)
            sb.append("${index + 1}. $cleanName\n")
            sb.append("   Vendor: ${sensor.vendor}\n")
            sb.append("   Power: ${sensor.power} mA | Max Range: ${sensor.maximumRange}\n\n")
        }

        binding.tvSensorListOverview.text = sb.toString()
    }
}
