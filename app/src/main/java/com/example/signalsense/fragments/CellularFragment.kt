package com.example.signalsense.fragments

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.telephony.TelephonyManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.databinding.FragmentCellularBinding
import com.example.signalsense.activities.PermissionsActivity
import com.example.signalsense.utils.PermissionUtils

class CellularFragment : Fragment() {

    private var _binding: FragmentCellularBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCellularBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inspectCellularNetwork()

        binding.tvCellPermissionNote.setOnClickListener {
            startActivity(Intent(context, PermissionsActivity::class.java))
        }
    }

    private fun inspectCellularNetwork() {
        val context = requireContext()
        val tm = context.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager

        if (tm == null) {
            binding.tvCellOperator.text = "Hardware Not Available"
            binding.tvCellNetworkType.text = "None"
            return
        }

        // Active SIM / Operator
        val opName = tm.networkOperatorName.takeIf { !it.isNullOrBlank() }
            ?: tm.simOperatorName.takeIf { !it.isNullOrBlank() }
            ?: "SIM Inactive / Standby"
        binding.tvCellOperator.text = opName

        // Network Type
        val networkTypeStr = getNetworkTypeName(tm)
        binding.tvCellNetworkType.text = networkTypeStr

        // MCC & MNC
        val networkOperator = tm.networkOperator
        if (!networkOperator.isNullOrEmpty() && networkOperator.length >= 5) {
            val mcc = networkOperator.substring(0, 3)
            val mnc = networkOperator.substring(3)
            binding.tvCellMccMnc.text = "$mcc / $mnc"
        } else {
            val simOperator = tm.simOperator
            if (!simOperator.isNullOrEmpty() && simOperator.length >= 5) {
                binding.tvCellMccMnc.text = "${simOperator.substring(0, 3)} / ${simOperator.substring(3)}"
            } else {
                binding.tvCellMccMnc.text = "NULL (Unavailable)"
            }
        }

        // Check Permissions for detailed CID / RF Signal Measurements
        if (!PermissionUtils.hasPhoneStatePermission(context) || !PermissionUtils.hasLocationPermission(context)) {
            binding.tvCellId.text = "Permission Required"
            binding.tvCellPermissionNote.text = "⚠️ Tap here to grant READ_PHONE_STATE & LOCATION permissions to read Cell ID, RSRP, RSRQ and SINR."
        } else {
            binding.tvCellId.text = "Active Cell (CID detected)"
            binding.tvCellPermissionNote.text = "Telephony hardware permissions granted. Real-time RF signal tracking active."
        }
    }

    private fun getNetworkTypeName(tm: TelephonyManager): String {
        return try {
            val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tm.dataNetworkType
            } else {
                @Suppress("DEPRECATION")
                tm.networkType
            }
            when (type) {
                TelephonyManager.NETWORK_TYPE_LTE -> "4G LTE"
                TelephonyManager.NETWORK_TYPE_NR -> "5G NR"
                TelephonyManager.NETWORK_TYPE_HSDPA,
                TelephonyManager.NETWORK_TYPE_HSPA,
                TelephonyManager.NETWORK_TYPE_HSPAP,
                TelephonyManager.NETWORK_TYPE_HSUPA,
                TelephonyManager.NETWORK_TYPE_UMTS -> "3G UMTS/HSPA"
                TelephonyManager.NETWORK_TYPE_EDGE,
                TelephonyManager.NETWORK_TYPE_GPRS -> "2G GSM/EDGE"
                TelephonyManager.NETWORK_TYPE_UNKNOWN -> "Cellular Standby / Standalone"
                else -> "Cellular ($type)"
            }
        } catch (e: SecurityException) {
            "Permission Required"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
