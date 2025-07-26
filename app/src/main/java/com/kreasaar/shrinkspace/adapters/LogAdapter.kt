package com.kreasaar.shrinkspace.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kreasaar.shrinkspace.R
import com.kreasaar.shrinkspace.data.LogEntry
import java.text.SimpleDateFormat
import java.util.*

class LogAdapter(
    private val onItemClick: (LogEntry) -> Unit
) : ListAdapter<LogEntry, LogAdapter.LogViewHolder>(LogDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_log, parent, false)
        return LogViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class LogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val actionText: TextView = itemView.findViewById(R.id.log_action)
        private val detailsText: TextView = itemView.findViewById(R.id.log_details)
        private val timestampText: TextView = itemView.findViewById(R.id.log_timestamp)

        fun bind(logEntry: LogEntry) {
            actionText.text = logEntry.action
            detailsText.text = logEntry.details
            timestampText.text = formatTimestamp(logEntry.timestamp)
            
            itemView.setOnClickListener { onItemClick(logEntry) }
        }

        private fun formatTimestamp(timestamp: Long): String {
            val date = Date(timestamp)
            val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
            return formatter.format(date)
        }
    }

    private class LogDiffCallback : DiffUtil.ItemCallback<LogEntry>() {
        override fun areItemsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: LogEntry, newItem: LogEntry): Boolean {
            return oldItem == newItem
        }
    }
} 