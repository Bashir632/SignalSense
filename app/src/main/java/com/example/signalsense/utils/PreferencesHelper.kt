package com.example.signalsense.utils

import android.content.Context
import android.content.SharedPreferences

class PreferencesHelper(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("signalsense_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_ONBOARDING_COMPLETED = "key_onboarding_completed"
        private const val KEY_SAMPLING_DELAY = "key_sampling_delay"
        private const val KEY_BACKEND_IP = "key_backend_ip"
        private const val KEY_BACKEND_PORT = "key_backend_port"
    }

    var isOnboardingCompleted: Boolean
        get() = prefs.getBoolean(KEY_ONBOARDING_COMPLETED, false)
        set(value) = prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETED, value).apply()

    var samplingDelayOption: Int
        get() = prefs.getInt(KEY_SAMPLING_DELAY, 0)
        set(value) = prefs.edit().putInt(KEY_SAMPLING_DELAY, value).apply()

    var backendIp: String
        get() = prefs.getString(KEY_BACKEND_IP, "192.168.1.100") ?: "192.168.1.100"
        set(value) = prefs.edit().putString(KEY_BACKEND_IP, value).apply()

    var backendPort: String
        get() = prefs.getString(KEY_BACKEND_PORT, "8000") ?: "8000"
        set(value) = prefs.edit().putString(KEY_BACKEND_PORT, value).apply()
}
