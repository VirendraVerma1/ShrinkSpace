package com.kreasaar.shrinkspace.ui.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kreasaar.shrinkspace.data.MediaItem
import com.kreasaar.shrinkspace.data.MediaRepository
import com.kreasaar.shrinkspace.utils.CompressionUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CompressionViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val compressionUtils: CompressionUtils
) : ViewModel() {

    private val _selectedItems = MutableLiveData<List<MediaItem>>()
    val selectedItems: LiveData<List<MediaItem>> = _selectedItems

    private val _compressionProgress = MutableLiveData<Int>()
    val compressionProgress: LiveData<Int> = _compressionProgress

    private val _isCompressing = MutableLiveData<Boolean>()
    val isCompressing: LiveData<Boolean> = _isCompressing

    private val _estimatedSavings = MutableLiveData<Long>()
    val estimatedSavings: LiveData<Long> = _estimatedSavings

    private val _compressionQuality = MutableLiveData<Int>()
    val compressionQuality: LiveData<Int> = _compressionQuality

    private var isCancelled = false

    init {
        _compressionQuality.value = 80
    }

    fun setSelectedItems(items: List<MediaItem>) {
        _selectedItems.value = items
        calculateEstimatedSavings()
    }

    fun startCompression() {
        viewModelScope.launch {
            try {
                _isCompressing.value = true
                isCancelled = false
                _compressionProgress.value = 0
                
                val items = _selectedItems.value ?: emptyList()
                val totalItems = items.size
                var processedItems = 0
                
                // Perform compression operations
                items.forEach { item ->
                    if (isCancelled) break
                    
                    // Compress each item
                    val success = withContext(Dispatchers.IO) {
                        val inputFile = java.io.File(item.uri.path ?: "")
                        val outputFile = java.io.File(inputFile.parent, "compressed_${inputFile.name}")
                        
                        when (item.type) {
                            "image" -> {
                                compressionUtils.compressImage(
                                    item.uri,
                                    outputFile,
                                    _compressionQuality.value ?: 80
                                )
                            }
                            "video" -> {
                                compressionUtils.compressVideo(
                                    item.uri,
                                    outputFile,
                                    _compressionQuality.value ?: 80
                                )
                            }
                            else -> false
                        }
                    }
                    
                    if (success) {
                        processedItems++
                        _compressionProgress.value = (processedItems * 100) / totalItems
                    }
                }
                
                if (!isCancelled) {
                    _compressionProgress.value = 100
                }
                
                _isCompressing.value = false
            } catch (e: Exception) {
                _isCompressing.value = false
            }
        }
    }

    fun setCompressionQuality(quality: Int) {
        _compressionQuality.value = quality.coerceIn(1, 100)
        calculateEstimatedSavings()
    }

    private fun calculateEstimatedSavings() {
        viewModelScope.launch {
            try {
                val items = _selectedItems.value ?: emptyList()
                val quality = _compressionQuality.value ?: 80
                
                var totalOriginalSize = 0L
                var totalCompressedSize = 0L
                
                items.forEach { item ->
                    val file = java.io.File(item.uri.path ?: "")
                    if (file.exists()) {
                        val originalSize = file.length()
                        totalOriginalSize += originalSize
                        
                        val estimatedCompressedSize = compressionUtils.estimateCompressedSize(originalSize, quality)
                        totalCompressedSize += estimatedCompressedSize
                    }
                }
                
                val savings = totalOriginalSize - totalCompressedSize
                _estimatedSavings.value = savings
            } catch (e: Exception) {
                _estimatedSavings.value = 0L
            }
        }
    }

    fun cancelCompression() {
        // Cancel ongoing compression
        isCancelled = true
        _isCompressing.value = false
    }

    fun getCompressionQualityString(): String {
        return compressionUtils.getCompressionQuality(_compressionQuality.value ?: 80)
    }

    fun formatFileSize(size: Long): String {
        return when {
            size >= 1024 * 1024 * 1024 -> "${size / (1024 * 1024 * 1024)} GB"
            size >= 1024 * 1024 -> "${size / (1024 * 1024)} MB"
            size >= 1024 -> "${size / 1024} KB"
            else -> "$size B"
        }
    }
} 