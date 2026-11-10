package com.example.signalsense.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemSensorCardBinding
import com.example.signalsense.models.SensorItem

class SensorsAdapter(
    private var sensors: List<SensorItem>,
    private val onToggleMonitoring: (SensorItem, Boolean) -> Unit
) : RecyclerView.Adapter<SensorsAdapter.SensorViewHolder>() {

    inner class SensorViewHolder(val binding: ItemSensorCardBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SensorViewHolder {
        val binding = ItemSensorCardBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SensorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SensorViewHolder, position: Int) {
        val item = sensors[position]
        holder.binding.tvSensorName.text = item.name
        holder.binding.tvSensorVendor.text = item.vendor
        holder.binding.tvSensorValues.text = item.currentValues
        holder.binding.tvSensorUnit.text = "Unit: ${item.unit} | Power: ${item.powerMa} mA"
        
        holder.binding.switchSensorMonitoring.setOnCheckedChangeListener(null)
        holder.binding.switchSensorMonitoring.isChecked = item.isMonitoring
        holder.binding.switchSensorMonitoring.setOnCheckedChangeListener { _, isChecked ->
            item.isMonitoring = isChecked
            onToggleMonitoring(item, isChecked)
        }
    }

    override fun getItemCount(): Int = sensors.size

    fun updateList(newList: List<SensorItem>) {
        sensors = newList
        notifyDataSetChanged()
    }
}
