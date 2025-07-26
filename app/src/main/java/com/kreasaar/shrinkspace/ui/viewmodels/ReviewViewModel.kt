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
class ReviewViewModel @Inject constructor(
    private val mediaRepository: MediaRepository,
    private val compressionUtils: CompressionUtils
) : ViewModel() {

    private val _reviewItems = MutableLiveData<List<MediaItem>>()
    val reviewItems: LiveData<List<MediaItem>> = _reviewItems

    private val _selectedItems = MutableLiveData<Set<Long>>()
    val selectedItems: LiveData<Set<Long>> = _selectedItems

    private val _isProcessing = MutableLiveData<Boolean>()
    val isProcessing: LiveData<Boolean> = _isProcessing

    private val _processingProgress = MutableLiveData<Int>()
    val processingProgress: LiveData<Int> = _processingProgress

    init {
        loadReviewItems()
    }

    private fun loadReviewItems() {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                
                // Load items flagged for review
                val allMedia = mediaRepository.getAllMedia()
                val flaggedItems = allMedia.filter { mediaItem ->
                    // Filter items that need review (e.g., large files, duplicates, etc.)
                    mediaItem.size > 10 * 1024 * 1024 || // Files larger than 10MB
                    mediaItem.type == "image" && mediaItem.size > 5 * 1024 * 1024 // Large images
                }
                
                _reviewItems.value = flaggedItems
                _isProcessing.value = false
            } catch (e: Exception) {
                _isProcessing.value = false
            }
        }
    }

    fun toggleItemSelection(itemId: Long) {
        val currentSelected = _selectedItems.value?.toMutableSet() ?: mutableSetOf()
        if (currentSelected.contains(itemId)) {
            currentSelected.remove(itemId)
        } else {
            currentSelected.add(itemId)
        }
        _selectedItems.value = currentSelected
    }

    fun selectAll() {
        val allIds = _reviewItems.value?.map { it.id }?.toSet() ?: emptySet()
        _selectedItems.value = allIds
    }

    fun clearSelection() {
        _selectedItems.value = emptySet()
    }

    fun getSelectedItems(): List<MediaItem> {
        val selectedIds = _selectedItems.value ?: emptySet()
        return _reviewItems.value?.filter { it.id in selectedIds } ?: emptyList()
    }

    fun deleteSelectedItems() {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _processingProgress.value = 0
                
                val itemsToDelete = getSelectedItems()
                val totalItems = itemsToDelete.size
                var processedItems = 0
                
                // Delete each selected item
                itemsToDelete.forEach { mediaItem ->
                    val file = java.io.File(mediaItem.uri.path ?: "")
                    if (file.exists()) {
                        val deleted = withContext(Dispatchers.IO) {
                            file.delete()
                        }
                        if (deleted) {
                            // Remove from repository
                            mediaRepository.deleteMedia(mediaItem)
                        }
                    }
                    processedItems++
                    _processingProgress.value = (processedItems * 100) / totalItems
                }
                
                // Refresh the review list
                loadReviewItems()
                clearSelection()
                
                _isProcessing.value = false
            } catch (e: Exception) {
                _isProcessing.value = false
            }
        }
    }

    fun compressSelectedItems(quality: Int = 80) {
        viewModelScope.launch {
            try {
                _isProcessing.value = true
                _processingProgress.value = 0
                
                val itemsToCompress = getSelectedItems()
                val totalItems = itemsToCompress.size
                var processedItems = 0
                
                // Compress each selected item
                itemsToCompress.forEach { mediaItem ->
                    val success = withContext(Dispatchers.IO) {
                        val inputFile = java.io.File(mediaItem.uri.path ?: "")
                        val outputFile = java.io.File(inputFile.parent, "compressed_${inputFile.name}")
                        
                        when (mediaItem.type) {
                            "image" -> {
                                compressionUtils.compressImage(
                                    mediaItem.uri,
                                    outputFile,
                                    quality
                                )
                            }
                            "video" -> {
                                compressionUtils.compressVideo(
                                    mediaItem.uri,
                                    outputFile,
                                    quality
                                )
                            }
                            else -> false
                        }
                    }
                    
                    if (success) {
                        // Update the media item with compressed file info
                        val compressedItem = mediaItem.copy(
                            uri = android.net.Uri.fromFile(java.io.File(mediaItem.uri.path?.replace(".", "_compressed.") ?: "")),
                            size = java.io.File(mediaItem.uri.path?.replace(".", "_compressed.") ?: "").length()
                        )
                        mediaRepository.updateMedia(compressedItem)
                    }
                    
                    processedItems++
                    _processingProgress.value = (processedItems * 100) / totalItems
                }
                
                // Refresh the review list
                loadReviewItems()
                clearSelection()
                
                _isProcessing.value = false
            } catch (e: Exception) {
                _isProcessing.value = false
            }
        }
    }

    fun refreshReviewList() {
        loadReviewItems()
    }

    fun getSelectedCount(): Int {
        return _selectedItems.value?.size ?: 0
    }

    fun isItemSelected(itemId: Long): Boolean {
        return _selectedItems.value?.contains(itemId) ?: false
    }
} 