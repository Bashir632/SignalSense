package com.example.signalsense.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.R
import com.example.databinding.ActivityOnboardingBinding
import com.example.signalsense.adapters.OnboardingAdapter
import com.example.signalsense.models.OnboardingPage
import com.example.signalsense.utils.PreferencesHelper

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val pages = listOf(
        OnboardingPage(
            title = "Hardware Sensor Telemetry",
            description = "Direct, precision telemetry from built-in Accelerometer, Gyroscope, Magnetometer, Barometer, Light, and Proximity hardware.",
            iconRes = R.drawable.ic_sensors
        ),
        OnboardingPage(
            title = "Cellular & Wi-Fi Signals",
            description = "Real-time monitoring of carrier SIM, network type, MCC/MNC, Cell ID, RSSI, RSRP, RSRQ, and local Wi-Fi beacon channels.",
            iconRes = R.drawable.ic_cellular
        ),
        OnboardingPage(
            title = "Local Storage & Data Export",
            description = "Save full measurement sessions to on-device Room SQLite database. Visualize signal graphs and export complete CSV & JSON datasets.",
            iconRes = R.drawable.ic_collection
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val adapter = OnboardingAdapter(pages)
        binding.viewPagerOnboarding.adapter = adapter

        setupDots(0)

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setupDots(position)
                if (position == pages.size - 1) {
                    binding.btnNext.text = getString(R.string.action_get_started)
                } else {
                    binding.btnNext.text = "Continue"
                }
            }
        })

        binding.btnNext.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current < pages.size - 1) {
                binding.viewPagerOnboarding.currentItem = current + 1
            } else {
                completeOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            completeOnboarding()
        }
    }

    private fun setupDots(activePosition: Int) {
        binding.dotsLayout.removeAllViews()
        for (i in pages.indices) {
            val dot = ImageView(this).apply {
                val size = if (i == activePosition) 24 else 8
                val height = 8
                val layoutParams = LinearLayout.LayoutParams(size, height).apply {
                    setMargins(6, 0, 6, 0)
                }
                this.layoutParams = layoutParams
                background = if (i == activePosition) {
                    ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.bg_badge_active)
                } else {
                    ContextCompat.getDrawable(this@OnboardingActivity, R.drawable.bg_badge_idle)
                }
            }
            binding.dotsLayout.addView(dot)
        }
    }

    private fun completeOnboarding() {
        PreferencesHelper(this).isOnboardingCompleted = true
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
