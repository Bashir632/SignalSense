package com.example.signalsense.fragments

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.databinding.FragmentSensorsBinding
import com.example.signalsense.adapters.SensorsAdapter
import com.example.signalsense.models.SensorItem
import java.util.Locale

class SensorsFragment : Fragment(), SensorEventListener {

    private var _binding: FragmentSensorsBinding? = null
    private val binding get() = _binding!!

    private var sensorManager: SensorManager? = null
    private lateinit var sensorsAdapter: SensorsAdapter
    private val sensorItemList = mutableListOf<SensorItem>()
    private val activeSensorTypes = mutableSetOf<Int>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSensorsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as? SensorManager
        setupRecyclerView()
        discoverSensors()

        binding.btnRefreshSensors.setOnClickListener {
            discoverSensors()
        }
    }

    private fun setupRecyclerView() {
        sensorsAdapter = SensorsAdapter(sensorItemList) { item, isMonitoring ->
            if (isMonitoring) {
                startMonitoringSensor(item.type)
            } else {
                stopMonitoringSensor(item.type)
            }
        }
        binding.rvSensors.layoutManager = LinearLayoutManager(context)
        binding.rvSensors.adapter = sensorsAdapter
    }

    private fun discoverSensors() {
        sensorItemList.clear()
        val sm = sensorManager ?: return

        // Check key telemetry sensors required by specifications
        val targetTypes = listOf(
            Triple(Sensor.TYPE_ACCELEROMETER, "Accelerometer", "m/s²"),
            Triple(Sensor.TYPE_GYROSCOPE, "Gyroscope", "rad/s"),
            Triple(Sensor.TYPE_MAGNETIC_FIELD, "Magnetometer", "μT"),
            Triple(Sensor.TYPE_LIGHT, "Light Sensor", "lux"),
            Triple(Sensor.TYPE_PROXIMITY, "Proximity Sensor", "cm"),
            Triple(Sensor.TYPE_PRESSURE, "Barometer", "hPa")
        )

        for ((type, fallbackName, unit) in targetTypes) {
            val sensor = sm.getDefaultSensor(type)
            if (sensor != null) {
                val cleanName = com.example.signalsense.utils.SensorUtils.sanitizeSensorName(sensor.name)
                sensorItemList.add(
                    SensorItem(
                        type = type,
                        name = cleanName,
                        vendor = sensor.vendor,
                        unit = unit,
                        powerMa = sensor.power
                    )
                )
            } else {
                // Safely show unavailable sensor
                sensorItemList.add(
                    SensorItem(
                        type = type,
                        name = "$fallbackName (Hardware Not Present)",
                        vendor = "None / Missing",
                        unit = unit,
                        powerMa = 0f,
                        currentValues = "Status: Hardware Not Available"
                    )
                )
            }
        }

        binding.tvSensorsCountSummary.text = "${sensorItemList.size} Primary Sensors Discovered"
        sensorsAdapter.updateList(sensorItemList)
    }

    private fun startMonitoringSensor(type: Int) {
        val sm = sensorManager ?: return
        val sensor = sm.getDefaultSensor(type) ?: return
        sm.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        activeSensorTypes.add(type)
    }

    private fun stopMonitoringSensor(type: Int) {
        val sm = sensorManager ?: return
        val sensor = sm.getDefaultSensor(type) ?: return
        sm.unregisterListener(this, sensor)
        activeSensorTypes.remove(type)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return
        val type = event.sensor.type
        val index = sensorItemList.indexOfFirst { it.type == type }
        if (index != -1) {
            val formatted = when (type) {
                Sensor.TYPE_ACCELEROMETER, Sensor.TYPE_GYROSCOPE, Sensor.TYPE_MAGNETIC_FIELD -> {
                    String.format(
                        Locale.getDefault(),
                        "X: %+.3f  Y: %+.3f  Z: %+.3f",
                        event.values[0],
                        event.values[1],
                        event.values[2]
                    )
                }
                Sensor.TYPE_LIGHT -> String.format(Locale.getDefault(), "Ambient: %.1f lux", event.values[0])
                Sensor.TYPE_PROXIMITY -> String.format(Locale.getDefault(), "Distance: %.1f cm", event.values[0])
                Sensor.TYPE_PRESSURE -> String.format(Locale.getDefault(), "Pressure: %.2f hPa", event.values[0])
                else -> event.values.joinToString(" ") { String.format(Locale.getDefault(), "%.2f", it) }
            }
            sensorItemList[index].currentValues = formatted
            sensorsAdapter.notifyItemChanged(index)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // No-op for direct monitoring
    }

    override fun onPause() {
        super.onPause()
        sensorManager?.unregisterListener(this)
        activeSensorTypes.clear()
        sensorItemList.forEach { it.isMonitoring = false }
        sensorsAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
