package com.example.signalsense.activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.R
import com.example.databinding.ActivityMainBinding
import com.example.signalsense.fragments.CellularFragment
import com.example.signalsense.fragments.CollectionFragment
import com.example.signalsense.fragments.DashboardFragment
import com.example.signalsense.fragments.SensorsFragment
import com.example.signalsense.fragments.WifiFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val dashboardFragment = DashboardFragment()
    private val sensorsFragment = SensorsFragment()
    private val cellularFragment = CellularFragment()
    private val wifiFragment = WifiFragment()
    private val collectionFragment = CollectionFragment()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(dashboardFragment, getString(R.string.title_dashboard))
        }
    }

    private fun setupToolbar() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_device_info -> {
                    startActivity(Intent(this, DeviceInfoActivity::class.java))
                    true
                }
                R.id.action_permissions -> {
                    startActivity(Intent(this, PermissionsActivity::class.java))
                    true
                }
                R.id.action_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                R.id.action_about -> {
                    startActivity(Intent(this, AboutActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_dashboard -> {
                    loadFragment(dashboardFragment, getString(R.string.title_dashboard))
                    true
                }
                R.id.navigation_sensors -> {
                    loadFragment(sensorsFragment, getString(R.string.title_sensors))
                    true
                }
                R.id.navigation_cellular -> {
                    loadFragment(cellularFragment, getString(R.string.title_cellular))
                    true
                }
                R.id.navigation_wifi -> {
                    loadFragment(wifiFragment, getString(R.string.title_wifi))
                    true
                }
                R.id.navigation_collection -> {
                    loadFragment(collectionFragment, getString(R.string.title_collection))
                    true
                }
                else -> false
            }
        }
    }

    fun switchTab(menuId: Int) {
        binding.bottomNavigation.selectedItemId = menuId
    }

    private fun loadFragment(fragment: Fragment, title: String) {
        binding.topAppBar.title = title
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}
