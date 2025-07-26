package com.kreasaar.shrinkspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.LogEntry
import com.kreasaar.shrinkspace.data.JobState
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.data.LogRepository
import com.kreasaar.shrinkspace.data.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val logRepository: LogRepository,
    private val jobRepository: JobRepository
) : ViewModel() {
    
    private val _mediaItems = MutableLiveData<List<MediaItem>>()
    val mediaItems: LiveData<List<MediaItem>> = _mediaItems
    
    private val _logs = MutableLiveData<List<LogEntry>>()
    val logs: LiveData<List<LogEntry>> = _logs
    
    private val _jobStates = MutableLiveData<List<JobState>>()
    val jobStates: LiveData<List<JobState>> = _jobStates
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadData()
    }
    
    private fun loadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load media items from repository
                val mediaItems = mediaRepository.getAllMedia()
                _mediaItems.value = mediaItems
                
                // Load logs from repository
                val logs = logRepository.getAllLogs()
                _logs.value = logs
                
                // Load job states from repository
                val jobStates = jobRepository.getAllJobs()
                _jobStates.value = jobStates
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    fun refreshData() {
        loadData()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getMediaItemById(id: Long): MediaItem? {
        return _mediaItems.value?.find { it.id == id }
    }
    
    fun getLogsByType(type: String): List<LogEntry> {
        return _logs.value?.filter { it.type == type } ?: emptyList()
    }
    
    fun getJobStateById(jobId: Long): JobState? {
        return _jobStates.value?.find { it.id == jobId }
    }
    
    fun getActiveJobs(): List<JobState> {
        return _jobStates.value?.filter { it.status == JobState.Status.RUNNING } ?: emptyList()
    }
    
    fun getCompletedJobs(): List<JobState> {
        return _jobStates.value?.filter { it.status == JobState.Status.COMPLETED } ?: emptyList()
    }
    
    fun getFailedJobs(): List<JobState> {
        return _jobStates.value?.filter { it.status == JobState.Status.FAILED } ?: emptyList()
    }
} 