package com.example.signalsense.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.R
import com.example.databinding.ActivityExportBinding
import org.json.JSONArray
import org.json.JSONObject

class ExportActivity : AppCompatActivity() {

    private lateinit var binding: ActivityExportBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityExportBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarExport.setNavigationOnClickListener {
            finish()
        }

        binding.btnGenerateExport.setOnClickListener {
            generateAndShareDataset()
        }
    }

    private fun generateAndShareDataset() {
        val isCsv = binding.rbCsv.isChecked
        val includeSensors = binding.cbExportSensors.isChecked
        val includeCellular = binding.cbExportCellular.isChecked
        val includeWifi = binding.cbExportWifi.isChecked

        if (!includeSensors && !includeCellular && !includeWifi) {
            Toast.makeText(this, "Please select at least one dataset to export", Toast.LENGTH_SHORT).show()
            return
        }

        val exportContent = if (isCsv) {
            buildCsvContent(includeSensors, includeCellular, includeWifi)
        } else {
            buildJsonContent(includeSensors, includeCellular, includeWifi)
        }

        val mimeType = if (isCsv) "text/csv" else "application/json"
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = mimeType
            putExtra(Intent.EXTRA_SUBJECT, "SignalSense Dataset Export")
            putExtra(Intent.EXTRA_TEXT, exportContent)
        }
        startActivity(Intent.createChooser(shareIntent, "Share SignalSense Dataset"))
    }

    private fun buildCsvContent(sensors: Boolean, cellular: Boolean, wifi: Boolean): String {
        val sb = StringBuilder()
        sb.append("timestamp,session_id,source,metric_1,metric_2,metric_3,signal_rssi\n")
        val now = System.currentTimeMillis()

        if (sensors) {
            sb.append("$now,Session_001,ACCELEROMETER,0.124,9.805,0.432,NULL\n")
            sb.append("${now + 1000},Session_001,GYROSCOPE,0.012,-0.005,0.002,NULL\n")
            sb.append("${now + 2000},Session_001,MAGNETOMETER,18.4,42.1,-33.5,NULL\n")
        }
        if (cellular) {
            sb.append("${now + 3000},Session_001,CELLULAR,Jazz_4G,MCC410,CID128472,-85\n")
        }
        if (wifi) {
            sb.append("${now + 4000},Session_001,WIFI,Office_5G,Channel36,5180MHz,-54\n")
        }
        return sb.toString()
    }

    private fun buildJsonContent(sensors: Boolean, cellular: Boolean, wifi: Boolean): String {
        val root = JSONObject()
        root.put("app", "SignalSense")
        root.put("version", "1.0.0")
        root.put("exported_at", System.currentTimeMillis())

        val records = JSONArray()
        val now = System.currentTimeMillis()

        if (sensors) {
            val r1 = JSONObject().apply {
                put("timestamp", now)
                put("session_id", "Session_001")
                put("source", "ACCELEROMETER")
                put("x", 0.124)
                put("y", 9.805)
                put("z", 0.432)
                put("unit", "m/s²")
            }
            records.put(r1)
        }
        if (cellular) {
            val r2 = JSONObject().apply {
                put("timestamp", now + 1000)
                put("session_id", "Session_001")
                put("source", "CELLULAR")
                put("operator", "Jazz 4G")
                put("rsrp_dbm", -98)
                put("rsrq_db", -11)
                put("cell_id", 128472)
            }
            records.put(r2)
        }
        if (wifi) {
            val r3 = JSONObject().apply {
                put("timestamp", now + 2000)
                put("session_id", "Session_001")
                put("source", "WIFI")
                put("ssid", "Office_5G")
                put("rssi_dbm", -54)
                put("frequency_mhz", 5180)
            }
            records.put(r3)
        }
        root.put("records", records)
        return root.toString(2)
    }
}
