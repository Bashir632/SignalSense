package com.example.signalsense.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.databinding.ItemSessionHistoryBinding
import com.example.signalsense.models.SessionSummary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter(
    private var sessions: List<SessionSummary>,
    private val onExportClick: (SessionSummary) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    inner class HistoryViewHolder(val binding: ItemSessionHistoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val binding = ItemSessionHistoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = sessions[position]
        holder.binding.tvSessionItemName.text = item.name
        val mins = item.durationSeconds / 60
        val secs = item.durationSeconds % 60
        holder.binding.tvSessionItemDuration.text = String.format(Locale.getDefault(), "%02d:%02d", mins, secs)
        holder.binding.tvSessionItemDate.text = dateFormat.format(Date(item.startTime))
        holder.binding.tvSessionItemRecords.text = "${item.totalRecords} records stored"

        holder.binding.btnExportSession.setOnClickListener {
            onExportClick(item)
        }
    }

    override fun getItemCount(): Int = sessions.size

    fun updateList(newList: List<SessionSummary>) {
        sessions = newList
        notifyDataSetChanged()
    }
}
