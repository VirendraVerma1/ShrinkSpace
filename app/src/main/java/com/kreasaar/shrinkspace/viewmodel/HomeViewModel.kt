package com.kreasaar.shrinkspace.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.utils.StorageUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val storageUtils: StorageUtils
) : ViewModel() {
    
    private val _storageInfo = MutableLiveData<StorageInfo>()
    val storageInfo: LiveData<StorageInfo> = _storageInfo
    
    private val _mediaCount = MutableLiveData<Int>()
    val mediaCount: LiveData<Int> = _mediaCount
    
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error
    
    init {
        loadHomeData()
    }
    
    private fun loadHomeData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                
                // Load storage statistics
                val storageInfo = loadStorageStatistics()
                _storageInfo.value = storageInfo
                
                // Load media count from repository
                val mediaCount = mediaRepository.getAllMedia().size
                _mediaCount.value = mediaCount
                
                _isLoading.value = false
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }
    
    private suspend fun loadStorageStatistics(): StorageInfo = withContext(Dispatchers.IO) {
        val totalSpace = storageUtils.getTotalStorage()
        val availableSpace = storageUtils.getAvailableStorage()
        val usedSpace = totalSpace - availableSpace
        val usagePercentage = ((usedSpace.toFloat() / totalSpace.toFloat()) * 100).toInt()
        
        StorageInfo(
            totalSpace = totalSpace,
            usedSpace = usedSpace,
            availableSpace = availableSpace,
            usagePercentage = usagePercentage
        )
    }
    
    fun refreshData() {
        loadHomeData()
    }
    
    fun clearError() {
        _error.value = null
    }
    
    data class StorageInfo(
        val totalSpace: Long,
        val usedSpace: Long,
        val availableSpace: Long,
        val usagePercentage: Int
    ) {
        fun getFormattedTotalSpace(): String {
            return formatFileSize(totalSpace)
        }
        
        fun getFormattedUsedSpace(): String {
            return formatFileSize(usedSpace)
        }
        
        fun getFormattedAvailableSpace(): String {
            return formatFileSize(availableSpace)
        }
        
        private fun formatFileSize(size: Long): String {
            return when {
                size >= 1024 * 1024 * 1024 -> "${size / (1024 * 1024 * 1024)} GB"
                size >= 1024 * 1024 -> "${size / (1024 * 1024)} MB"
                size >= 1024 -> "${size / 1024} KB"
                else -> "$size B"
            }
        }
    }
} 