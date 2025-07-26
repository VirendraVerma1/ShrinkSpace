package com.kreasaar.shrinkspace.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "log_entries")
data class LogEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String,
    val timestamp: Long,
    val details: String
) 