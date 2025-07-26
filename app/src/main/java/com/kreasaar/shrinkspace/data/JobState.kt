package com.kreasaar.shrinkspace.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "job_states")
data class JobState(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String,
    val progress: Int,
    val status: String,
    val lastUpdated: Long
) 