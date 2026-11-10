package com.example.signalsense.activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.R
import com.example.databinding.ActivityPermissionsBinding
import com.example.signalsense.utils.PermissionUtils

class PermissionsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionsBinding

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        checkAndUpdatePermissionStatuses()
        val allGranted = results.values.all { it }
        if (allGranted) {
            Toast.makeText(this, "All telemetry permissions granted!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Some telemetry permissions were denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPermissionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbarPermissions.setNavigationOnClickListener {
            finish()
        }

        checkAndUpdatePermissionStatuses()

        binding.btnRequestAllPerms.setOnClickListener {
            val perms = PermissionUtils.getAllRequiredPermissions()
            permissionLauncher.launch(perms)
        }
    }

    override fun onResume() {
        super.onResume()
        checkAndUpdatePermissionStatuses()
    }

    private fun checkAndUpdatePermissionStatuses() {
        // Location
        val hasLoc = PermissionUtils.hasLocationPermission(this)
        updateStatusView(binding.tvLocationPermStatus, hasLoc)

        // Phone State
        val hasPhone = PermissionUtils.hasPhoneStatePermission(this)
        updateStatusView(binding.tvPhonePermStatus, hasPhone)

        // Notification
        val hasNotif = PermissionUtils.hasNotificationPermission(this)
        updateStatusView(binding.tvNotificationPermStatus, hasNotif)
    }

    private fun updateStatusView(view: android.widget.TextView, isGranted: Boolean) {
        if (isGranted) {
            view.text = "Granted"
            view.setTextColor(ContextCompat.getColor(this, R.color.status_active))
            view.setBackgroundResource(R.drawable.bg_badge_active)
        } else {
            view.text = "Not Granted"
            view.setTextColor(ContextCompat.getColor(this, R.color.status_stopped))
            view.setBackgroundResource(R.drawable.bg_badge_idle)
        }
    }
}
