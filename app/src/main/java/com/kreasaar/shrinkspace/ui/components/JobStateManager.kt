package com.kreasaar.shrinkspace.ui.components

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class JobStateManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "job_states", Context.MODE_PRIVATE
    )
    private val gson = Gson()
    
    // Save job state to persistent storage
    fun saveJobState(jobId: String, state: JobState) {
        val jobStates = getJobStates().toMutableMap()
        jobStates[jobId] = state
        val json = gson.toJson(jobStates)
        prefs.edit().putString("job_states", json).apply()
    }
    
    // Load job state from persistent storage
    fun getJobState(jobId: String): JobState? {
        val jobStates = getJobStates()
        return jobStates[jobId]
    }
    
    // Load all job states from persistent storage
    fun getJobStates(): Map<String, JobState> {
        val json = prefs.getString("job_states", "{}")
        val type = object : TypeToken<Map<String, JobState>>() {}.type
        return gson.fromJson(json, type) ?: emptyMap()
    }
    
    // Update job state in persistent storage
    fun updateJobState(jobId: String, state: JobState) {
        saveJobState(jobId, state)
    }
    
    // Remove job state
    fun removeJobState(jobId: String) {
        val jobStates = getJobStates().toMutableMap()
        jobStates.remove(jobId)
        val json = gson.toJson(jobStates)
        prefs.edit().putString("job_states", json).apply()
    }
    
    // Clear all job states
    fun clearAllJobStates() {
        prefs.edit().remove("job_states").apply()
    }
    
    data class JobState(
        val jobId: String,
        val status: Status,
        val progress: Int = 0,
        val totalItems: Int = 0,
        val processedItems: Int = 0,
        val errorMessage: String? = null,
        val startTime: Long = System.currentTimeMillis(),
        val endTime: Long? = null
    ) {
        enum class Status {
            PENDING,
            RUNNING,
            COMPLETED,
            FAILED,
            CANCELLED
        }
    }
} 