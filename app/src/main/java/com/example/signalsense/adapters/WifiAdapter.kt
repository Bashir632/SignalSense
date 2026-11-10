package com.example.signalsense.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemWifiApBinding
import com.example.signalsense.models.WifiItem

class WifiAdapter(
    private var accessPoints: List<WifiItem>
) : RecyclerView.Adapter<WifiAdapter.WifiViewHolder>() {

    inner class WifiViewHolder(val binding: ItemWifiApBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WifiViewHolder {
        val binding = ItemWifiApBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WifiViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WifiViewHolder, position: Int) {
        val item = accessPoints[position]
        holder.binding.tvApSsid.text = if (item.ssid.isBlank()) "[Hidden SSID]" else item.ssid
        holder.binding.tvApBssid.text = "BSSID: ${item.bssid}"
        holder.binding.tvApFreq.text = "${item.frequencyMhz} MHz (Ch ${item.channel})"
        holder.binding.tvApRssi.text = "${item.rssiDbm} dBm"
    }

    override fun getItemCount(): Int = accessPoints.size

    fun updateList(newList: List<WifiItem>) {
        accessPoints = newList
        notifyDataSetChanged()
    }
}
