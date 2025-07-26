package com.kreasaar.shrinkspace.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.LogEntry
import com.kreasaar.shrinkspace.data.LogRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityLogViewModel @Inject constructor(
    private val logRepository: LogRepository
) : ViewModel() {

    private val _logs = MutableLiveData<List<LogEntry>>()
    val logs: LiveData<List<LogEntry>> = _logs

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _canUndo = MutableLiveData<Boolean>()
    val canUndo: LiveData<Boolean> = _canUndo

    init {
        loadLogs()
    }

    private fun loadLogs() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                // Load log entries from repository
                val logEntries = logRepository.getAllLogs()
                _logs.value = logEntries
                checkUndoAvailability()
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun undoLastAction() {
        viewModelScope.launch {
            try {
                // Implement undo functionality
                val currentLogs = _logs.value?.toMutableList() ?: mutableListOf()
                if (currentLogs.isNotEmpty()) {
                    val lastLog = currentLogs.last()
                    if (lastLog.isUndoable) {
                        // Perform undo operation based on log type
                        when (lastLog.type) {
                            "delete" -> {
                                // Restore deleted file
                                // Implementation depends on your file restoration logic
                            }
                            "compress" -> {
                                // Restore original file
                                // Implementation depends on your compression logic
                            }
                        }
                        // Remove the log entry after undo
                        currentLogs.removeAt(currentLogs.lastIndex)
                        _logs.value = currentLogs
                        checkUndoAvailability()
                    }
                }
            } catch (e: Exception) {
                // Handle undo error
            }
        }
    }

    fun clearAllLogs() {
        viewModelScope.launch {
            try {
                // Clear all log entries
                logRepository.deleteAllLogs()
                _logs.value = emptyList()
                _canUndo.value = false
            } catch (e: Exception) {
                // Handle clear error
            }
        }
    }

    fun addLogEntry(
        type: String,
        message: String,
        details: String? = null,
        isUndoable: Boolean = false
    ) {
        viewModelScope.launch {
            try {
                val logEntry = LogEntry(
                    type = type,
                    message = message,
                    details = details,
                    timestamp = System.currentTimeMillis(),
                    isUndoable = isUndoable
                )
                
                // Save log entry to repository
                logRepository.insertLog(logEntry)
                
                // Update the logs list
                val currentLogs = _logs.value?.toMutableList() ?: mutableListOf()
                currentLogs.add(logEntry)
                _logs.value = currentLogs
                
                checkUndoAvailability()
            } catch (e: Exception) {
                // Handle log entry error
            }
        }
    }

    private fun checkUndoAvailability() {
        // Check if undo is available
        val currentLogs = _logs.value
        val canUndo = currentLogs?.any { it.isUndoable } ?: false
        _canUndo.value = canUndo
    }

    fun filterLogsByType(type: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val filteredLogs = if (type == "all") {
                    logRepository.getAllLogs()
                } else {
                    logRepository.getLogsByType(type)
                }
                _logs.value = filteredLogs
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
            }
        }
    }

    fun refreshLogs() {
        loadLogs()
    }
} 